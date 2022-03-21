package com.liuhy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.liuhy.config.JavaCVAudioProperties;
import com.liuhy.enums.JavaCvConstants;
import com.liuhy.enums.WorkStatusEnum;
import com.liuhy.model.AudioPushRequest;
import com.liuhy.model.AudioPushTask;
import com.liuhy.model.FileToStreamRequest;
import com.liuhy.model.StreamConvertRequest;
import com.liuhy.model.StreamToFileRequest;
import com.liuhy.model.WorkerStatus;
import com.liuhy.service.AudioPushService;
import com.liuhy.service.Callback;
import com.liuhy.worker.AudioWorkerThreadPool;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
* 推流服务
* @author liuhy
* @version 1.0
*/
@Service
@Slf4j
public class AudioPushServiceImpl implements AudioPushService {

    @Autowired
    private JavaCVAudioProperties javaCVProperties;
    
    @Autowired
    private AudioWorkerThreadPool workerThreadPool;

	public AudioPushServiceImpl(JavaCVAudioProperties javaCVProperties, AudioWorkerThreadPool workerThreadPool) {
		super();
		this.javaCVProperties = javaCVProperties;
		this.workerThreadPool = workerThreadPool;
	}
	
	private boolean checkCommonParams(AudioPushRequest audioPushBase) {
		if(audioPushBase == null) {
    		log.error("【javacv】参数错误,对象不能为空");
    		return false;
		}else if(StrUtil.isBlank(audioPushBase.getId())) {
    		log.error("【javacv】参数错误,任务id不能为空");
    		return false;
    	}else if(StrUtil.isBlank(audioPushBase.getPushDestUrl())) {
    		log.error("【javacv】参数错误,目标流地址不能为空");
    		return false;
    	}else if(CollectionUtil.isEmpty(audioPushBase.getPushSrcUrlList())){
    		log.error("【javacv】参数错误,源文件地址不能为空");
    		return false;
    	}
		return true;
	}
	
	/**
	 * 设置参数
	 * @param audioPushRequest
	 * @param startCall
	 * @param endCall
	 * @return
	 */
	private AudioPushTask getAudioPushTask(AudioPushRequest audioPushRequest, Callback startCall, Callback endCall) {
		AudioPushTask audioPushTask = new AudioPushTask();
		if(audioPushRequest.getBitRate() == null) {
			audioPushRequest.setBitRate(javaCVProperties.getBitRate());
		}
		if(audioPushRequest.getChannels() == null) {
			audioPushRequest.setChannels(javaCVProperties.getChannels());
		}
		if(audioPushRequest.getFrameRate() == null) {
			audioPushRequest.setFrameRate(javaCVProperties.getFrameRate());
		}
		if(audioPushRequest.getSampleRate() == null) {
			audioPushRequest.setSampleRate(javaCVProperties.getSampleRate());
		}
		if(audioPushRequest.getIsLoop() == null) {
			audioPushRequest.setIsLoop(javaCVProperties.getIsLoop());
		}
		if(audioPushRequest.getCodecName() == null) {
			audioPushRequest.setCodecName(javaCVProperties.getCodecName());
		}
		audioPushTask.setAudioPushRequest(audioPushRequest);
    	audioPushTask.setStartCallback(startCall);
    	audioPushTask.setEndCallback(endCall);
    	return audioPushTask;
	}
	
	@Override
	public WorkerStatus createFileToStreamTask(FileToStreamRequest fileToStreamRequest, Callback startCall, Callback endCall) {
		AudioPushRequest audioPushRequest = new AudioPushRequest();
		BeanUtil.copyProperties(fileToStreamRequest, audioPushRequest);
		audioPushRequest.setPushSrcUrlList(fileToStreamRequest.getSrcFileUrlList());
		audioPushRequest.setPushDestUrl(fileToStreamRequest.getDestStreamUrl());
		if(!checkCommonParams(audioPushRequest)) {
    		return WorkerStatus.builer(WorkStatusEnum.PARAMSERRROR);
    	}
		String destStreamType = StrUtil.subBefore(audioPushRequest.getPushDestUrl(), JavaCvConstants.PROTOCOL_CHAR, false);
		if(!JavaCvConstants.ENABLE_STREAM_TYPE_LIST.contains(destStreamType)) {
    		log.error("【javacv】参数错误,目标流地址{}格式不支持",destStreamType);
    		return WorkerStatus.builer(WorkStatusEnum.PARAMSERRROR);
    	}
		audioPushRequest.setId(StrUtil.concat(false, JavaCvConstants.FTS_TASK_PREFIX,audioPushRequest.getId()));
        return workerThreadPool.execPushTask(getAudioPushTask(audioPushRequest, startCall, endCall));
	}

    @Override
    public WorkerStatus createFileToStreamTask(FileToStreamRequest fileToStreamRequest, Callback endCall) {
    	return createFileToStreamTask(fileToStreamRequest, null, endCall);
    }
    
    @Override
    public WorkerStatus createFileToStreamTask(FileToStreamRequest fileToStreamRequest) {
    	return createFileToStreamTask(fileToStreamRequest, null, null);
    }
    
    @Override
    public WorkerStatus stopFileToStreamTask(String taskId) {
        return workerThreadPool.stopAudioPushWorker(StrUtil.concat(false, JavaCvConstants.FTS_TASK_PREFIX,taskId));
    }

    @Override
    public WorkerStatus fileToStreamTaskStatus(String taskId) {
        return workerThreadPool.getAudioPushWorkerStatus(StrUtil.concat(false, JavaCvConstants.FTS_TASK_PREFIX,taskId));
    }
    
	@Override
	public WorkerStatus createStreamConvertTask(StreamConvertRequest streamConvertRequest, Callback startCall, Callback endCall) {
		AudioPushRequest audioPushRequest = new AudioPushRequest();
		BeanUtil.copyProperties(streamConvertRequest, audioPushRequest);
		audioPushRequest.setPushSrcUrlList(CollectionUtil.newArrayList(streamConvertRequest.getSrcStreamUrl()));
		audioPushRequest.setPushDestUrl(streamConvertRequest.getDestStreamUrl());
		if(!checkCommonParams(audioPushRequest)) {
    		return WorkerStatus.builer(WorkStatusEnum.PARAMSERRROR);
    	}
		String srcStreamType = StrUtil.subBefore(streamConvertRequest.getSrcStreamUrl(), JavaCvConstants.PROTOCOL_CHAR, false);
		String destStreamType = StrUtil.subBefore(streamConvertRequest.getDestStreamUrl(), JavaCvConstants.PROTOCOL_CHAR, false);
		if(!JavaCvConstants.ENABLE_STREAM_TYPE_LIST.contains(srcStreamType)) {
    		log.error("【javacv】参数错误,源流地址{}格式不支持",srcStreamType);
    		return WorkerStatus.builer(WorkStatusEnum.PARAMSERRROR);
    	}
		if(!JavaCvConstants.ENABLE_STREAM_TYPE_LIST.contains(destStreamType)) {
    		log.error("【javacv】参数错误,目标流地址{}格式不支持",destStreamType);
    		return WorkerStatus.builer(WorkStatusEnum.PARAMSERRROR);
    	}
		audioPushRequest.setId(StrUtil.concat(false, JavaCvConstants.STS_TASK_PREFIX,audioPushRequest.getId()));
        return workerThreadPool.execPushTask(getAudioPushTask(audioPushRequest, startCall, endCall));
	}

    @Override
    public WorkerStatus createStreamConvertTask(StreamConvertRequest streamConvertRequest, Callback endCall) {
    	return createStreamConvertTask(streamConvertRequest, null, endCall);
    }
    
    @Override
    public WorkerStatus createStreamConvertTask(StreamConvertRequest streamConvertRequest) {
    	return createStreamConvertTask(streamConvertRequest, null, null);
    }
    
    @Override
    public WorkerStatus stopStreamConvertTask(String taskId) {
        return workerThreadPool.stopAudioPushWorker(StrUtil.concat(false, JavaCvConstants.STS_TASK_PREFIX,taskId));
    }

    @Override
    public WorkerStatus streamConvertTaskStatus(String taskId) {
        return workerThreadPool.getAudioPushWorkerStatus(StrUtil.concat(false, JavaCvConstants.STS_TASK_PREFIX,taskId));
    }
    
	@Override
	public WorkerStatus createStreamToFileTask(StreamToFileRequest streamToFileRequest, Callback startCall, Callback endCall) {
		AudioPushRequest audioPushRequest = new AudioPushRequest();
		BeanUtil.copyProperties(streamToFileRequest, audioPushRequest);
		audioPushRequest.setPushSrcUrlList(CollectionUtil.newArrayList(streamToFileRequest.getSrcStreamUrl()));
		audioPushRequest.setPushDestUrl(streamToFileRequest.getDestFilePath());
		if(!checkCommonParams(audioPushRequest)) {
    		return WorkerStatus.builer(WorkStatusEnum.PARAMSERRROR);
    	}
		String srcStreamType = StrUtil.subBefore(streamToFileRequest.getSrcStreamUrl(), JavaCvConstants.PROTOCOL_CHAR, false);
		if(!JavaCvConstants.ENABLE_STREAM_TYPE_LIST.contains(srcStreamType)) {
    		log.error("【javacv】参数错误,源流地址{}格式不支持",srcStreamType);
    		return WorkerStatus.builer(WorkStatusEnum.PARAMSERRROR);
    	}
		audioPushRequest.setId(StrUtil.concat(false, JavaCvConstants.STF_TASK_PREFIX,audioPushRequest.getId()));
        return workerThreadPool.execPushTask(getAudioPushTask(audioPushRequest, startCall, endCall));
	}

    @Override
    public WorkerStatus createStreamToFileTask(StreamToFileRequest streamToFileRequest, Callback endCall) {
    	return createStreamToFileTask(streamToFileRequest, null, endCall);
    }
    
    @Override
    public WorkerStatus createStreamToFileTask(StreamToFileRequest streamToFileRequest) {
    	return createStreamToFileTask(streamToFileRequest, null, null);
    }
    
    @Override
    public WorkerStatus stopStreamToFileTask(String taskId) {
        return workerThreadPool.stopAudioPushWorker(StrUtil.concat(false, JavaCvConstants.STF_TASK_PREFIX,taskId));
    }

    @Override
    public WorkerStatus streamToFileTaskStatus(String taskId) {
        return workerThreadPool.getAudioPushWorkerStatus(StrUtil.concat(false, JavaCvConstants.STF_TASK_PREFIX,taskId));
    }
	
}
