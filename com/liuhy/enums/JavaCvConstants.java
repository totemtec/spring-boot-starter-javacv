package com.liuhy.enums;

import java.util.List;

import cn.hutool.core.collection.CollectionUtil;

/**
* 常量类
* @author liuhy
* @version 1.1
*/
public class JavaCvConstants {
	
	/**
	 * 支持的流类型常量
	 */
	public static final String[] ENABLE_STREAM_TYPE_ARRAY= new String[] {"rtmp","rtsp","rtp","udp"};

	/**
	 * 支持的流类型常量
	 */
	public static final List<String> ENABLE_STREAM_TYPE_LIST = CollectionUtil.newArrayList(ENABLE_STREAM_TYPE_ARRAY);
	
	/**
	 * 推流任务ID前缀
	 */
	public static final String FTS_TASK_PREFIX = "FTS_";
	
	/**
	 * 保存流为文件任务ID前缀
	 */
	public static final String STF_TASK_PREFIX = "STF_";
	
	/**
	 * 流转换任务ID前缀
	 */
	public static final String STS_TASK_PREFIX = "STS_";
	
	/**
	 * 符号常量
	 */
	public static final String PROTOCOL_CHAR = "://";
	
	
}