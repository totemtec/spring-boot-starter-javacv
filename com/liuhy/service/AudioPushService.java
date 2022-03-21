package com.liuhy.service;

import com.liuhy.model.FileToStreamRequest;
import com.liuhy.model.StreamConvertRequest;
import com.liuhy.model.StreamToFileRequest;
import com.liuhy.model.WorkerStatus;

/**
* 推流类
* @author liuhy
* @version 1.0
*/
public interface AudioPushService {

    /**
     * 启动一个文件转流任务,自定义推流参数（配置优先规则：代码-配置文件-源文件配置）
     * @param fileToStreamRequest 参数
     * @return WorkerStatus 结果
     */
    WorkerStatus createFileToStreamTask(FileToStreamRequest fileToStreamRequest);
    
    /**
     * 启动一个文件转流任务,自定义推流参数（配置优先规则：代码-配置文件-取源文件自身参数）-带回调
     * @param fileToStreamRequest 参数
	 * @param endCall -推流结束回调-同步
     * @return WorkerStatus 结果
     */
    WorkerStatus createFileToStreamTask(FileToStreamRequest fileToStreamRequest, Callback endCall);
    
    /**
     * 启动一个文件转流任务,自定义推流参数（配置优先规则：代码-配置文件-取源文件自身参数）-带回调
     * @param fileToStreamRequest 参数
	 * @param startCall -推流开始回调-异步
	 * @param endCall -推流结束回调-同步
     * @return WorkerStatus 结果
     */
    WorkerStatus createFileToStreamTask(FileToStreamRequest fileToStreamRequest, Callback startCall, Callback endCall);
    
    /**
     * 停止指定的文件转流任务
     *
     * @param taskId 参数
     * @return WorkerStatus 结果
     */
    WorkerStatus stopFileToStreamTask(String taskId);

    /**
     * 获取指定文件转流任务的工作状态
     *
     * @param taskId 参数
     * @return WorkerStatus 结果
     */
    WorkerStatus fileToStreamTaskStatus(String taskId);
    
    /**
     * 启动一个流转换任务,自定义推流参数（配置优先规则：代码-配置文件-源文件配置）
     * @param streamConvertRequest 参数
     * @return WorkerStatus 结果
     */
    WorkerStatus createStreamConvertTask(StreamConvertRequest streamConvertRequest);
    
    /**
     * 启动一个流转换任务,自定义推流参数（配置优先规则：代码-配置文件-取源文件自身参数）-带回调
     * @param streamConvertRequest 参数
	 * @param endCall -推流结束回调-同步
     * @return WorkerStatus 结果
     */
    WorkerStatus createStreamConvertTask(StreamConvertRequest streamConvertRequest, Callback endCall);
    
    /**
     * 启动一个流转换任务,自定义推流参数（配置优先规则：代码-配置文件-取源文件自身参数）-带回调
     * @param streamConvertRequest 参数
	 * @param startCall -推流开始回调-异步
	 * @param endCall -推流结束回调-同步
     * @return WorkerStatus 结果
     */
    WorkerStatus createStreamConvertTask(StreamConvertRequest streamConvertRequest, Callback startCall, Callback endCall);
    
    /**
     * 停止指定的流转换任务
     *
     * @param taskId 参数
     * @return WorkerStatus 结果
     */
    WorkerStatus stopStreamConvertTask(String taskId);

    /**
     * 获取指定流转换任务的工作状态
     *
     * @param taskId 参数
     * @return WorkerStatus 结果
     */
    WorkerStatus streamConvertTaskStatus(String taskId);
    
    /**
     * 启动一个流保存任务,自定义推流参数（配置优先规则：代码-配置文件-源文件配置）
     * @param streamToFileRequest 参数
     * @return WorkerStatus 结果
     */
    WorkerStatus createStreamToFileTask(StreamToFileRequest streamToFileRequest);
    
    /**
     * 启动一个流保存任务,自定义推流参数（配置优先规则：代码-配置文件-取源文件自身参数）-带回调
     * @param streamToFileRequest 参数
	 * @param endCall -推流结束回调-同步
     * @return WorkerStatus 结果
     */
    WorkerStatus createStreamToFileTask(StreamToFileRequest streamToFileRequest, Callback endCall);
    
    /**
     * 启动一个流保存任务,自定义推流参数（配置优先规则：代码-配置文件-取源文件自身参数）-带回调
     * @param streamToFileRequest 参数
	 * @param startCall -推流开始回调-异步
	 * @param endCall -推流结束回调-同步
     * @return WorkerStatus 结果
     */
    WorkerStatus createStreamToFileTask(StreamToFileRequest streamToFileRequest, Callback startCall, Callback endCall);
    
    /**
     * 停止指定的流保存任务
     *
     * @param taskId 参数
     * @return WorkerStatus 结果
     */
    WorkerStatus stopStreamToFileTask(String taskId);

    /**
     * 获取指定流保存任务的工作状态
     *
     * @param taskId 参数
     * @return WorkerStatus 结果
     */
    WorkerStatus streamToFileTaskStatus(String taskId);

}
