package com.liuhy.service;

import java.io.File;

import com.liuhy.enums.AudioCodeEnum;
import com.liuhy.model.AudioFileInfo;

/**
* 音频参数转换（包含采样率、编码，位数，通道数）
* @author liuhy
* @version 1.0
*/
public interface AudioConvertService {
	
	/**
	 * 获取音频文件详细信息(非mp3文件时长获取不准确，请用getAudioFileDuration方法获取时长)
	 * @param audioFile -音频文件
	 * @return AudioFileInfo 音频信息
	 */
	AudioFileInfo getAudioFileInfo(File audioFile);
	
	/**
	 * 获取音频文件详细信息(非mp3文件时长获取不准确，请用getAudioFileDuration方法获取时长)
	 * @param audioFilePath -音频文件路径
	 * @return AudioFileInfo 音频信息
	 */
	AudioFileInfo getAudioFileInfo(String audioFilePath);
	
	/**
	 * 获取音频文件的时长;
	 * 1. 如果是mp3文件，执行同步获取并回调(mp3文件时长获取推荐使用getAudioFileInfo);
	 * 2. 如果是非mp3文件，执行异步获取并回调;
	 * @param audioFilePath -音频文件路径
     * @return Long 文件时长(微妙)
	 */
	Long getAudioFileDuration(String audioFilePath);
	
	/**
	 * 获取音频文件的时长;
	 * 1. 如果是mp3文件，执行同步获取并回调(mp3文件时长获取推荐使用getAudioFileInfo);
	 * 2. 如果是非mp3文件，执行异步获取并回调;
	 * @param audioFile -音频文件
     * @return Long 文件时长(微妙)
	 */
	Long getAudioFileDuration(File audioFile);
	
	/**
	 * 获取音频文件的时长--异步带回调;
	 * 1. 如果是mp3文件，执行同步获取并回调(mp3文件时长获取推荐使用getAudioFileInfo);
	 * 2. 如果是非mp3文件，执行异步获取并回调;
	 * @param audioFilePath -音频文件路径
	 * @param callback -回调方法
	 */
	void getAudioFileDuration(String audioFilePath, Callback<Long> callback);
	
	/**
	 * 获取音频文件的时长--异步带回调;
	 * 1. 如果是mp3文件，执行同步获取并回调(mp3文件时长获取推荐使用getAudioFileInfo);
	 * 2. 如果是非mp3文件，执行异步获取并回调;
	 * @param audioFile -音频文件
	 * @param callback -回调方法
	 */
	void getAudioFileDuration(File audioFile, Callback<Long> callback);
	
	/**
	 * 通用音频格式参数转换
	 * 
	 * @param inputFilePath
	 *            -音频文件路径
	 * @param audioCodec
	 *            -音频编码
     * @return File 文件
	 */
	File convert(String inputFilePath, AudioCodeEnum audioCodec);
	
	/**
	 * 通用音频格式参数转换
	 * 
	 * @param inputFile
	 *            -音频文件
	 * @param audioCodec
	 *            -音频编码
     * @return File 文件
	 */
	File convert(File inputFile, AudioCodeEnum audioCodec);
	
	/**
	 * 通用音频格式参数转换
	 * 
	 * @param inputFilePath
	 *            -音频文件路径
	 * @param audioCodec
	 *            -音频编码
	 * @param audioBitrate
	 *            -音频比特率
     * @return File 文件
	 */
	File convert(String inputFilePath, AudioCodeEnum audioCodec, Integer audioBitrate);
	
	/**
	 * 通用音频格式参数转换
	 * 
	 * @param inputFile
	 *            -音频文件
	 * @param audioCodec
	 *            -音频编码
	 * @param audioBitrate
	 *            -音频比特率
     * @return File 文件
	 */
	File convert(File inputFile, AudioCodeEnum audioCodec, Integer audioBitrate);
	
	/**
	 * 通用音频格式参数转换-带回调
	 * 
	 * @param inputFilePath
	 *            -音频文件路径
	 * @param audioCodec
	 *            -音频编码
	 * @param sampleRate
	 *            -音频采样率
	 * @param audioBitrate
	 *            -音频比特率
	 * @param audioChannels
	 *            -通道号
     * @return File 文件
	 */
	File convert(String inputFilePath, AudioCodeEnum audioCodec, Integer sampleRate, Integer audioBitrate, Integer audioChannels);
	
	/**
	 * 通用音频格式参数转换-带回调
	 * 
	 * @param inputFile
	 *            -音频文件
	 * @param audioCodec
	 *            -音频编码
	 * @param sampleRate
	 *            -音频采样率
	 * @param audioBitrate
	 *            -音频比特率
	 * @param audioChannels
	 *            -通道号
     * @return File 文件
	 */
	File convert(File inputFile, AudioCodeEnum audioCodec, Integer sampleRate, Integer audioBitrate, Integer audioChannels);
	
	/**
	 * 通用音频格式参数转换
	 * 
	 * @param inputFilePath
	 *            -音频文件路径
	 * @param outputFilePath
	 *            -音频文件路径
	 * @param audioCodec
	 *            -音频编码
	 */
	public void convert(String inputFilePath, String outputFilePath, AudioCodeEnum audioCodec);
	
	/**
	 * 通用音频格式参数转换
	 * 
	 * @param inputFile
	 *            -音频文件
	 * @param outputFilePath
	 *            -音频文件路径
	 * @param audioCodec
	 *            -音频编码
	 */
	public void convert(File inputFile, String outputFilePath, AudioCodeEnum audioCodec);
	
	/**
	 * 通用音频格式参数转换
	 * 
	 * @param inputFilePath
	 *            -音频文件路径
	 * @param outputFilePath
	 *            -音频文件路径
	 * @param audioCodec
	 *            -音频编码
	 * @param audioBitrate
	 *            -音频比特率
	 */
	public void convert(String inputFilePath, String outputFilePath, AudioCodeEnum audioCodec, Integer audioBitrate);
	
	/**
	 * 通用音频格式参数转换
	 * 
	 * @param inputFile
	 *            -音频文件
	 * @param outputFilePath
	 *            -音频文件路径
	 * @param audioCodec
	 *            -音频编码
	 * @param audioBitrate
	 *            -音频比特率
	 */
	public void convert(File inputFile, String outputFilePath, AudioCodeEnum audioCodec, Integer audioBitrate);
	
	/**
	 * 通用音频格式参数转换-带回调
	 * 
	 * @param inputFilePath
	 *            -音频文件路径
	 * @param outputFilePath
	 *            -音频文件路径
	 * @param audioCodec
	 *            -音频编码
	 * @param sampleRate
	 *            -音频采样率
	 * @param audioBitrate
	 *            -音频比特率
	 * @param audioChannels
	 *            -通道号
	 */
	public void convert(String inputFilePath, String outputFilePath, AudioCodeEnum audioCodec, Integer sampleRate, Integer audioBitrate, Integer audioChannels);
	
	/**
	 * 通用音频格式参数转换-带回调
	 * 
	 * @param inputFile
	 *            -音频文件路径
	 * @param outputFilePath
	 *            -音频文件路径
	 * @param audioCodec
	 *            -音频编码
	 * @param sampleRate
	 *            -音频采样率
	 * @param audioBitrate
	 *            -音频比特率
	 * @param audioChannels
	 *            -通道号
	 */
	public void convert(File inputFile, String outputFilePath, AudioCodeEnum audioCodec, Integer sampleRate, Integer audioBitrate, Integer audioChannels);
	
	/**
	 * 通用音频格式参数转换
	 * 
	 * @param inputFilePath
	 *            -音频文件路径
	 * @param outputFilePath
	 *            -音频文件路径
	 * @param successCall -成功回调方法
	 * @param failCall -失败回调方法
	 */
	public void syncConvert(String inputFilePath, String outputFilePath, Callback successCall, Callback failCall);
	
	/**
	 * 通用音频格式参数转换
	 * 
	 * @param inputFile
	 *            -音频文件
	 * @param outputFile
	 *            -音频文件
	 * @param successCall -成功回调方法
	 * @param failCall -失败回调方法
	 */
	public void syncConvert(File inputFile, File outputFile, Callback successCall, Callback failCall);
	
	/**
	 * 通用音频格式参数转换
	 * 
	 * @param inputFile
	 *            -音频文件
	 * @param outputFile
	 *            -音频文件
	 * @param audioCodec
	 *            -音频编码
	 * @param successCall -成功回调方法
	 * @param failCall -失败回调方法
	 */
	public void syncConvert(File inputFile, File outputFile, AudioCodeEnum audioCodec, Callback successCall, Callback failCall);
	
	/**
	 * 通用音频格式参数转换
	 * 
	 * @param inputFilePath
	 *            -音频文件路径
	 * @param outputFilePath
	 *            -音频文件路径
	 * @param audioCodec
	 *            -音频编码
	 * @param successCall -成功回调方法
	 * @param failCall -失败回调方法
	 */
	public void syncConvert(String inputFilePath, String outputFilePath, AudioCodeEnum audioCodec, Callback successCall, Callback failCall);
	
	/**
	 * 通用音频格式参数转换
	 * 
	 * @param inputFilePath -音频文件路径
	 * @param outputFilePath -音频文件路径
	 * @param audioCodec -音频编码
	 * @param audioBitrate -音频比特率
	 * @param successCall -成功回调方法
	 * @param failCall -失败回调方法
	 */
	public void syncConvert(String inputFilePath, String outputFilePath, AudioCodeEnum audioCodec, Integer audioBitrate, Callback successCall, Callback failCall);
	
	/**
	 * 通用音频格式参数转换
	 * 
	 * @param inputFile -音频文件
	 * @param outputFile -音频文件
	 * @param audioCodec -音频编码
	 * @param audioBitrate -音频比特率
	 * @param successCall -成功回调方法
	 * @param failCall -失败回调方法
	 */
	public void syncConvert(File inputFile, File outputFile, AudioCodeEnum audioCodec, Integer audioBitrate, Callback successCall, Callback failCall);
	
	/**
	 * 通用音频格式参数转换-带回调
	 * 
	 * @param inputFilePath
	 *            -音频文件路径
	 * @param outputFilePath
	 *            -音频文件路径
	 * @param audioCodec
	 *            -音频编码
	 * @param sampleRate
	 *            -音频采样率
	 * @param audioBitrate
	 *            -音频比特率
	 * @param audioChannels
	 *            -通道号
	 * @param successCall -成功回调方法
	 * @param failCall -失败回调方法
	 */
	public void syncConvert(String inputFilePath, String outputFilePath, AudioCodeEnum audioCodec, Integer sampleRate, Integer audioBitrate, Integer audioChannels, Callback successCall, Callback failCall);
	
	/**
	 * 通用音频格式参数转换-带回调
	 * 
	 * @param inputFile
	 *            -音频文件
	 * @param outputFile
	 *            -音频文件
	 * @param audioCodec
	 *            -音频编码
	 * @param sampleRate
	 *            -音频采样率
	 * @param audioBitrate
	 *            -音频比特率
	 * @param audioChannels
	 *            -通道号
	 * @param successCall -成功回调方法
	 * @param failCall -失败回调方法
	 */
	public void syncConvert(File inputFile, File outputFile, AudioCodeEnum audioCodec, Integer sampleRate, Integer audioBitrate, Integer audioChannels, Callback successCall, Callback failCall);
}