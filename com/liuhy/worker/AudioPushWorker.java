package com.liuhy.worker;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import com.liuhy.cache.CacheUtil;
import com.liuhy.enums.AudioCodeEnum;
import com.liuhy.enums.JavaCvConstants;
import com.liuhy.enums.StreamFormatEnum;
import com.liuhy.model.AudioPushRequest;
import com.liuhy.model.AudioPushTask;
import com.liuhy.util.HttpsUtil;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

/**
* 推流者
* @author liuhy
* @version 1.0
*/
@Slf4j
public class AudioPushWorker implements Runnable {


    /**
     * 描述推流任务
     */
    private AudioPushTask pushTask = null;
    /**
     * 采集流
     */
    private FFmpegFrameGrabber grabber = null;
    /**
     * 编码推送流
     */
    private FFmpegFrameRecorder recorder = null;

    /**
     * 控制该进程是否进行
     */
    private volatile boolean isRunning = false;
    /**
     * 循环次数
     */
    private volatile int times = 1;
    
    /**
     * 记录推流进程的启动时间，用于计算运行时长
     */
    private long startTime = System.currentTimeMillis();

    AudioPushWorker(AudioPushTask pushTask) {
        this.pushTask = pushTask;
    }

    public void stop() {
        this.isRunning = false;
    }

    public boolean isRunning(){
        return isRunning;
    }

    /**
     * 获取推流进程的运行时长
     * @return 时长
     */
    public long getRunningTime(){
        return System.currentTimeMillis() - startTime;
    }

    @Override
    public void run() {
        try {
            go();
        } catch (Exception e) {
            log.error("【javacv】推流服务异常",e);
        }
        CacheUtil.AUDIOPUSHWORKERMAP.remove(pushTask.getAudioPushRequest().getId());
        log.debug("【javacv】从cache移除，id={}",pushTask.getAudioPushRequest().getId());
    }

    public void startGrabber() throws Exception {
    	if(StrUtil.startWithAny(pushTask.getAudioPushRequest().getPushSrcUrlList().get(0), JavaCvConstants.ENABLE_STREAM_TYPE_ARRAY)) {
    		grabber = FFmpegFrameGrabber.createDefault(pushTask.getAudioPushRequest().getPushSrcUrlList().get(0));
    	}else {
        	List<InputStream> streams = CollectionUtil.newArrayList();
        	for (String url : pushTask.getAudioPushRequest().getPushSrcUrlList()) {
    			if(HttpUtil.isHttps(url)) {
    				streams.add(HttpsUtil.getStream(new URL(url)));
    			}else if(HttpUtil.isHttp(url)) {
    				streams.add(URLUtil.getStream(new URL(url)));
    			}else if(FileUtil.exist(url)) {
    				streams.add(FileUtil.getInputStream(url));
    			}else {
    				log.error("【javacv】input:{}文件不存在",url);
    				throw new FileNotFoundException(url);
    			}
    		}
        	InputStream sequenceInputStream = new SequenceInputStream(Collections.enumeration(streams));
        	grabber = new FFmpegFrameGrabber(sequenceInputStream);
    	}
        grabber.setOption("stimeout", "20000");
        try {
            grabber.start();
        } catch (Exception e) {
        	try {
                grabber.restart();  //一次重启尝试
            } catch (Exception e2) {
                log.error("【javacv】启动拉流进程失败，请检查拉流地址",e);
            }
        }
        Frame grabframe = grabber.grab();  //获取一帧
        if (grabframe != null) {
        	log.debug("【javacv】获取到第一帧");
        }else {
        	log.error("【javacv】无法获取到第一帧");
        }
        log.info("【javacv】grab input File codecCode:{},audioChannels:{},duration:{},audioBitrate:{},sampleRate:{}",grabber.getAudioCodec(),grabber.getAudioChannels(),grabber.getFormatContext().duration(),grabber.getAudioBitrate(),grabber.getSampleRate());
    }

    public void startPush() throws Exception {
    	int channels = grabber.getAudioChannels();
    	AudioPushRequest audioPushRequest = pushTask.getAudioPushRequest();
    	if(audioPushRequest.getChannels() != null) {
    		channels = audioPushRequest.getChannels();
        }
    	if(channels!=1 && channels!=2) {
    		channels = 2;
    	}
    	String pushDescUrl = audioPushRequest.getPushDestUrl();
        recorder = new FFmpegFrameRecorder(pushDescUrl, channels);
        // 封装格式
        String streamType = StrUtil.subBefore(pushDescUrl, JavaCvConstants.PROTOCOL_CHAR, false);
        recorder.setFormat(StreamFormatEnum.getCodeByName(streamType));
    	// 音频比特率
        if(audioPushRequest.getBitRate() != null) {
        	recorder.setAudioBitrate(audioPushRequest.getBitRate());
        }else {
        	recorder.setAudioBitrate(grabber.getAudioBitrate());
        }
 		// 音频采样率
        if(audioPushRequest.getSampleRate() != null) {
        	recorder.setSampleRate(audioPushRequest.getSampleRate());
        }else {
        	recorder.setSampleRate(grabber.getSampleRate());
        }
        if(recorder.getSampleRate()==16000 && audioPushRequest.getPushDestUrl().startsWith("rtmp")) {
    		log.warn("【javacv】FLV Stream does not support sample rate 16000, choose from (44100, 22050, 11025)");
    		recorder.setSampleRate(44100);
    	}
 		//通道数
        recorder.setAudioChannels(channels);
        //音频编码/解码
        if(StrUtil.isNotBlank(audioPushRequest.getCodecName())) {
        	int myCode = AudioCodeEnum.getCodeByDesc(audioPushRequest.getCodecName());
        	if(myCode == 0) {
        		recorder.setAudioCodec(grabber.getAudioCodec());
        	}else {
        		recorder.setAudioCodec(myCode);
        	}
        }else {
        	recorder.setAudioCodec(grabber.getAudioCodec());
        }
 		
 		// 关键帧间隔，一般与帧率相同或者是帧率的两倍
        //recorder.setGopSize(pushTask.getFrameRate());
        // 最高质量
     	//recorder.setAudioQuality(0);
     	// 不可变(固定)音频比特率
     	//recorder.setAudioOption("crf", "0");
        if("rtsp".equalsIgnoreCase(streamType)) {
        	recorder.setOption("rtsp_transport", "tcp"); // 使用tcp的方式，不然会丢包很严重
        }
     	recorder.setOption("flvflags", "no_duration_filesize");
     	long startTime1 = System.currentTimeMillis();
        try {
            recorder.start();
        } catch (Exception e) {
            try {
                if (recorder != null) {  //尝试重启录制器
                    recorder.stop();
                    recorder.start();
                }
            } catch (java.lang.Exception e1) {
                log.error("【javacv】recorder开启失败，请检查拉流地址",e1);
            }
        }
        log.info("【javacv】recorder.start()耗时:{}ms",System.currentTimeMillis()-startTime1);
        log.info("【javacv】record output File codecCode:{},audioBitrate:{},channels:{},sampleRate:{},frameRate:{}",recorder.getAudioCodec(),recorder.getAudioBitrate(),recorder.getAudioChannels(),recorder.getSampleRate(),audioPushRequest.getFrameRate());
    }
    
    public void cancel() {
    	log.info("【javacv】taskId={},推流结束,结束时间:{},循环次数:{},耗时:{}ms",pushTask.getAudioPushRequest().getId(),DateUtil.now(),times,getRunningTime());
    	isRunning = false;
    	//推流结束回调
    	if(pushTask.getEndCallback()!=null) {
    		pushTask.getEndCallback().doCallback(pushTask.getEndCallback().getParam());
    	}
    }
    
    public void go() throws Exception {
    	try {
	        startTime = System.currentTimeMillis();
	        startGrabber();
	        startPush();
	        grabber.flush();
	        isRunning = true;
	        Frame grabframe = null;
	        times = 1;
	        log.info("【javacv】taskId={},推流开始，开始时间:{}",pushTask.getAudioPushRequest().getId(), DateUtil.now());
	        //推流开始回调
	        if(pushTask.getStartCallback() != null) {
		        ThreadUtil.execute(new Runnable() {
					@Override
					public void run() {
						pushTask.getStartCallback().doCallback(pushTask.getStartCallback().getParam());
					}
				});
	        }
	        while (isRunning) {
	        	try {
					grabframe = grabber.grab();
				} catch (org.bytedeco.javacv.FFmpegFrameGrabber.Exception e) {
					log.warn("【javacv】 grab()获取到非音频帧，忽略{}",e.getMessage());
					continue;
				}
                if(pushTask.getAudioPushRequest().getCancelTime()!=null && new Date().getTime()>=pushTask.getAudioPushRequest().getCancelTime().getTime()) {
                	log.info("【javacv】taskId={},结束时间到，取消推流",pushTask.getAudioPushRequest().getId());
                	cancel();
                }else if(grabframe == null && !pushTask.getAudioPushRequest().getIsLoop()) {
                	log.info("【javacv】taskId={},一次推流完成，取消推流",pushTask.getAudioPushRequest().getId());
                	cancel();
                }else if(grabframe == null) {
                	++times;
                	log.info("【javacv】taskId={},循环推流，第{}次循环",pushTask.getAudioPushRequest().getId(),times);
                	//循环推流，从头开始
                	grabber.restart();
                	grabber.grab();
                	grabber.flush();
                }else {
                	//recorder.setTimestamp(grabber.getTimestamp());
                	recorder.record(grabframe);
                }
	            TimeUnit.MILLISECONDS.sleep(pushTask.getAudioPushRequest().getFrameRate()); //帧率设置;
	        }
        } catch (InterruptedException e) {
            log.error("【javacv】taskId={},推流进程出现异常", pushTask.getAudioPushRequest().getId(),e);
            isRunning = false;
        }finally {
        	if(grabber!=null) {
        		grabber.close();
        	}
            if(recorder!=null) {
            	recorder.close();
            }
        }
    }

}
