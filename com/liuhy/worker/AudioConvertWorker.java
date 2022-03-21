package com.liuhy.worker;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.FrameRecorder.Exception;

import com.liuhy.enums.AudioCodeEnum;
import com.liuhy.service.Callback;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

/**
* 音频转换者
* @author liuhy
* @version 1.0
*/
@Slf4j
public class AudioConvertWorker implements Runnable {

    /**
     * 采集流
     */
    private FFmpegFrameGrabber grabber = null;
    /**
     * 编码推送流
     */
    private FFmpegFrameRecorder recorder = null;
    
    private String inputFilePath;
    
    private String outputFilePath;
    
    private AudioCodeEnum audioCodec;
    
    private Integer sampleRate;
    
    private Integer audioBitrate;
    
    private Integer audioChannels;
    
    private Integer type; //1-转码，2-获取文件时长
    
    private Callback successCall;
    
    private Callback failCall;

    /**
     * 控制该进程是否进行
     */
    private volatile boolean isRunning = false;
    /**
     * 记录推流进程的启动时间，用于计算运行时长
     */
    private long startTime = System.currentTimeMillis();

   public AudioConvertWorker(String inputFilePath, String outputFilePath, AudioCodeEnum audioCodec, Integer sampleRate, Integer audioBitrate, Integer audioChannels, Integer type, Callback successCallback, Callback failCallback) {
        this.inputFilePath = inputFilePath;
        this.outputFilePath = outputFilePath;
        this.audioCodec = audioCodec;
        this.sampleRate = sampleRate;
        this.audioBitrate = audioBitrate;
        this.audioChannels = audioChannels;
        this.successCall = successCallback;
        this.failCall = failCallback;
        this.type = type;
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
			Frame audioSamples = null;
			// 音频录制（输出地址，音频通道）
			//抓取器
			grabber = new FFmpegFrameGrabber(inputFilePath);
			int audioChannel = 2;
			// 开启抓取器
			if (start(grabber)) {
				audioChannel = grabber.getAudioChannels();
				if(audioChannels != null) {
					audioChannel = audioChannels;
				}
				if(audioChannel!=1 && audioChannel!=2) {
					audioChannel = 2;
		    	}
				recorder = new FFmpegFrameRecorder(outputFilePath, audioChannel);
				recorder.setAudioOption("crf", "0");
				int srcAudioCodec = grabber.getAudioCodec();
				if(audioCodec == null) {
					recorder.setAudioCodec(srcAudioCodec);
				}else {
					recorder.setAudioCodec(audioCodec.getCode());
				}
				if(audioBitrate == null) {
					recorder.setAudioBitrate(grabber.getAudioBitrate());
				}else {
					recorder.setAudioBitrate(audioBitrate);
				}
				recorder.setAudioChannels(audioChannel);
				if(sampleRate == null) {
					recorder.setSampleRate(grabber.getSampleRate());
				}else {
					recorder.setSampleRate(sampleRate);
				}
				//recorder.setAudioQuality(0);
				//recorder.setAudioOption("aq", "10");
				// 开启录制器
				if (start(recorder)) {
					audioSamples = grabber.grab();  //获取一帧不要
					// 抓取音频
					while ((audioSamples = grabber.grab()) != null) {
						//recorder.setTimestamp(grabber.getTimestamp());
						recorder.record(audioSamples);
					}
					log.info("【javacv】转码成功，outputFile audioCodec:{},sampleRate:{},bitrate:{},channel:{},耗时:{}ms", recorder.getAudioCodec(), recorder.getSampleRate(), recorder.getAudioBitrate(), recorder.getAudioChannels(), getRunningTime());
					if(type==1) {
						successCall.doCallback(successCall.getParam());
					}else {
						close(grabber);
						close(recorder);
						grabber = new FFmpegFrameGrabber(outputFilePath);
						start(grabber);
						System.out.print(outputFilePath);
						successCall.doCallback(grabber.getFormatContext().duration());
						FileUtil.del(outputFilePath);
						close(grabber);
					}
				}
			}
		} catch (Exception | org.bytedeco.javacv.FrameGrabber.Exception e) {
			log.error("【javacv】转码失败",e);
			if(type==1) {
				failCall.doCallback(failCall.getParam());
			}else {
				successCall.doCallback(0L);
			}
		}finally {
			if(grabber!=null) {
				close(grabber);
			}
			if(recorder!=null) {
				close(recorder);
			}
		}
	}

    private boolean start(FrameGrabber grabber) throws org.bytedeco.javacv.FrameGrabber.Exception {
		try {
			grabber.start();
			return true;
		} catch (org.bytedeco.javacv.FrameGrabber.Exception e2) {
			try {
				log.error("【javacv】首次打开抓取器失败，准备重启抓取器...",e2);
				grabber.restart();
				return true;
			} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
				try {
					log.error("【javacv】重启抓取器失败，正在关闭抓取器...",e);
					grabber.stop();
				} catch (org.bytedeco.javacv.FrameGrabber.Exception e1) {
					log.error("【javacv】停止抓取器失败！",e1);
				}
				throw e;
			}
		}
	}
 
	private  boolean start(FrameRecorder recorder) throws Exception {
		try {
			recorder.start();
			return true;
		} catch (Exception e2) {
			try {
				log.error("【javacv】首次打开录制器失败！准备重启录制器...",e2);
				recorder.stop();
				recorder.start();
				return true;
			} catch (Exception e) {
				try {
					log.error("【javacv】重启录制器失败！正在停止录制器...",e);
					recorder.stop();
				} catch (Exception e1) {
					log.error("【javacv】关闭录制器失败！",e1);
				}
				throw e;
			}
		}
	}
 
	private  boolean close(FrameGrabber grabber) {
		try {
			grabber.flush();
			grabber.close();
			return true;
		} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
			return false;
		} finally {
			try {
				grabber.close();
			} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
				log.error("【javacv】关闭抓取器失败",e);
			}
		}
	}
 
	private boolean close(FrameRecorder recorder) {
		try {
			recorder.close();
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			try {
				recorder.close();
			} catch (Exception e) {
 
			}
		}
	}

}
