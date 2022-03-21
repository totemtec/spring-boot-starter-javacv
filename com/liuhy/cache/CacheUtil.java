package com.liuhy.cache;

import com.liuhy.worker.AudioPushWorker;

import cn.hutool.cache.impl.TimedCache;

/**
* 缓存配置
* @author liuhy
* @version 1.0
*/
public final class CacheUtil {

	/**
	 * 存储ID与PushWorker之间的映射
	 * ID为PushTask的ID
	 */
	public static TimedCache<String, AudioPushWorker> AUDIOPUSHWORKERMAP = cn.hutool.cache.CacheUtil.newTimedCache(12*60*60*1000);
	
	/*
	 * 保存服务启动时间
	 */
	public static long STARTTIME;

}
