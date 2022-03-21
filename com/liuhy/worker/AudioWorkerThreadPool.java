package com.liuhy.worker;


import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.liuhy.cache.CacheUtil;
import com.liuhy.config.JavaCVThreadPoolProperties;
import com.liuhy.enums.WorkStatusEnum;
import com.liuhy.model.AudioPushTask;
import com.liuhy.model.WorkerStatus;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

/**
* 音频处理线程池
* @author liuhy
* @version 1.0
*/
@Component
@Slf4j
public class AudioWorkerThreadPool {

    /**
     * java提供的线程池技术
     */
    private ThreadPoolExecutor poolExecutor = null;
    
    /**
     * 参数类
     */
    @Autowired
    private JavaCVThreadPoolProperties threadPoolProperties;

   public AudioWorkerThreadPool(JavaCVThreadPoolProperties threadPoolProperties) {
	   this.threadPoolProperties = threadPoolProperties;
	   init();
   }
   
   /**
	 * 初始化
	 */
   public void init() {
	   if(poolExecutor != null) {
		   return;
	   }
	   ThreadFactory threadFactory = new ThreadFactoryBuilder().setNamePrefix("javacv-thread-").build();
       poolExecutor = new ThreadPoolExecutor(threadPoolProperties.getCorePoolSize(), threadPoolProperties.getMaximumPoolSize(), threadPoolProperties.getKeepAliveTime(),
               TimeUnit.MICROSECONDS, new LinkedBlockingDeque<Runnable>(), threadFactory);
   }

    /**
     * 提交任务到线程池
     * @param pushTask 推流任务
     * @return WorkerStatus 返回结果
     */
    public WorkerStatus execPushTask(AudioPushTask pushTask) {
        WorkerStatus status = new WorkerStatus();
        AudioPushWorker AudioPushWorker = null;
        if(CacheUtil.AUDIOPUSHWORKERMAP.containsKey(pushTask.getAudioPushRequest().getId())){
            AudioPushWorker = CacheUtil.AUDIOPUSHWORKERMAP.get(pushTask.getAudioPushRequest().getId());
            status.setRunningTime(AudioPushWorker.getRunningTime());
            if(AudioPushWorker.isRunning()){
                status.setWorkerStatus(WorkStatusEnum.RUNNING.getStatus());
            }else{
                status.setWorkerStatus(WorkStatusEnum.WAITING.getStatus());
            }
            return status;
        }
        AudioPushWorker = new AudioPushWorker(pushTask);
        CacheUtil.AUDIOPUSHWORKERMAP.put(pushTask.getAudioPushRequest().getId(),AudioPushWorker);
        poolExecutor.execute(AudioPushWorker);
        if(AudioPushWorker.isRunning()){
            status.setWorkerStatus(WorkStatusEnum.RUNNING.getStatus());
        }else if(!CacheUtil.AUDIOPUSHWORKERMAP.containsKey(pushTask.getAudioPushRequest().getId())){
            status.setWorkerStatus(WorkStatusEnum.STOPPED.getStatus());
        }else{
            status.setWorkerStatus(WorkStatusEnum.WAITING.getStatus());
        }
        status.setRunningTime(AudioPushWorker.getRunningTime());
        return status;
    }
    
    /**
     * 提交任务到线程池
     *
     * @param convertTask 参数
     * @return WorkerStatus 返回结果
     */
    public WorkerStatus execConvertTask(AudioConvertWorker convertTask) {
        poolExecutor.execute(convertTask);
        return WorkerStatus.builer(WorkStatusEnum.RUNNING);
    }

    /**
     * 基于id，停止指定的推流
     *
     * @param taskId 任务id
     * @return WorkerStatus 返回结果
     */
    public WorkerStatus stopAudioPushWorker(String taskId) {
        WorkerStatus status = new WorkerStatus();
        if (!CacheUtil.AUDIOPUSHWORKERMAP.containsKey(taskId)) {
            status.setWorkerStatus(WorkStatusEnum.NOTEXIST.getStatus());
            log.error("【javacv】taskId={},任务不存在",taskId);
        }else{
            AudioPushWorker worker = CacheUtil.AUDIOPUSHWORKERMAP.get(taskId);
            worker.stop();
            status.setRunningTime(worker.getRunningTime());
            status.setWorkerStatus(WorkStatusEnum.STOPPED.getStatus());
            log.info("【javacv】taskId={},停止推流成功,推流时长:{}ms",taskId,worker.getRunningTime());
        }
        return status;
    }

    /**
     * 获取指定推流的推流状态
     * @param taskId 任务id
     * @return WorkerStatus 返回结果
     */
    public WorkerStatus getAudioPushWorkerStatus(String taskId){
        WorkerStatus status = new WorkerStatus();
        if(!CacheUtil.AUDIOPUSHWORKERMAP.containsKey(taskId)){
            status.setWorkerStatus(WorkStatusEnum.NOTEXIST.getStatus());
        }else{
            AudioPushWorker worker = CacheUtil.AUDIOPUSHWORKERMAP.get(taskId);
            if(worker.isRunning()){
                status.setWorkerStatus(WorkStatusEnum.RUNNING.getStatus());
                status.setRunningTime(worker.getRunningTime());
            }else{
                status.setWorkerStatus(WorkStatusEnum.WAITING.getStatus());
            }
        }
        return status;
    }


}
