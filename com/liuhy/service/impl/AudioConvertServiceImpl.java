package com.liuhy.service.impl;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.FrameRecorder.Exception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.liuhy.enums.AudioCodeEnum;
import com.liuhy.enums.JavaCvConstants;
import com.liuhy.model.AudioFileInfo;
import com.liuhy.service.AudioConvertService;
import com.liuhy.service.Callback;
import com.liuhy.worker.AudioConvertWorker;
import com.liuhy.worker.AudioWorkerThreadPool;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 转码服务
 * 
 * @author liuhy
 * @version 1.0
 */
@Service
@Slf4j
public class AudioConvertServiceImpl implements AudioConvertService {

	@Autowired
	private AudioWorkerThreadPool workerThreadPool;

	public AudioConvertServiceImpl() {
		super();
	}

	public AudioConvertServiceImpl(AudioWorkerThreadPool workerThreadPool) {
		super();
		this.workerThreadPool = workerThreadPool;
	}

	private File getConvertFile(String inputFilePath, String outputFilePath, AudioCodeEnum audioCodec,
			Integer sampleRate, Integer audioBitrate, Integer audioChannels) {
		File outputFile = new File(outputFilePath);
		FileUtil.mkParentDirs(outputFile);
		// 音频录制（输出地址，音频通道）
		FFmpegFrameRecorder recorder = null;
		// 抓取器
		FFmpegFrameGrabber grabber = null;
		try {
			if (!FileUtil.exist(inputFilePath)) {
				log.error("【javacv】文件:{}不存在", inputFilePath);
				return null;
			}
			long startTime = System.currentTimeMillis();
			Frame audioSamples = null;
			// 抓取器
			grabber = new FFmpegFrameGrabber(inputFilePath);
			int audioChannel = 2;
			// 开启抓取器
			if (start(grabber)) {
				audioChannel = grabber.getAudioChannels();
				if (audioChannels != null) {
					audioChannel = audioChannels;
				}
				if (audioChannel != 1 && audioChannel != 2) {
					audioChannel = 2;
				}
				recorder = new FFmpegFrameRecorder(outputFilePath, audioChannel);
				recorder.setAudioOption("crf", "0");
				int srcAudioCodec = grabber.getAudioCodec();
				if (audioCodec == null) {
					recorder.setAudioCodec(srcAudioCodec);
				} else {
					recorder.setAudioCodec(audioCodec.getCode());
				}
				if (audioBitrate == null) {
					recorder.setAudioBitrate(grabber.getAudioBitrate());
				} else {
					recorder.setAudioBitrate(audioBitrate);
				}
				recorder.setAudioChannels(audioChannel);
				if (sampleRate == null) {
					recorder.setSampleRate(grabber.getSampleRate());
				} else {
					recorder.setSampleRate(sampleRate);
				}
				//recorder.setAudioQuality(0);
				//recorder.setAudioOption("aq", "10");
				// 开启录制器
				if (start(recorder)) {
					audioSamples = grabber.grab(); // 获取一帧不要
					// 抓取音频
					while ((audioSamples = grabber.grab()) != null) {
						recorder.setTimestamp(grabber.getTimestamp());
						recorder.record(audioSamples);
					}
					log.info("【javacv】转码成功，outputFile audioCodec:{},sampleRate:{},bitrate:{},channel:{},耗时:{}ms", recorder.getAudioCodec(), recorder.getSampleRate(), recorder.getAudioBitrate(), recorder.getAudioChannels(), System.currentTimeMillis() - startTime);
				}
			}
		} catch (Exception | org.bytedeco.javacv.FrameGrabber.Exception e) {
			log.error("【javacv】转码失败", e);
			return null;
		} finally {
			if (grabber != null) {
				close(grabber);
			}
			if (recorder != null) {
				close(recorder);
			}
		}
		return outputFile;
	}

	@Override
	public AudioFileInfo getAudioFileInfo(String audioFilePath) {
		if (!FileUtil.exist(audioFilePath)) {
			log.error("【javacv】文件:{}不存在", audioFilePath);
			return null;
		}
		FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(audioFilePath);
		try {
			grabber.start();
		} catch (org.bytedeco.javacv.FrameGrabber.Exception e1) {
			try {
				grabber.restart(); // 一次重启尝试
			} catch (org.bytedeco.javacv.FrameGrabber.Exception e2) {
				log.error("【javacv】获取音频文件信息错误", e2);
			}
		}
		AudioFileInfo audioFileInfo = new AudioFileInfo();
		audioFileInfo.setBitRate(grabber.getAudioBitrate());
		audioFileInfo.setChannels(grabber.getAudioChannels());
		audioFileInfo.setCodecName(AudioCodeEnum.getDescByCode(grabber.getAudioCodec()));
		audioFileInfo.setSampleRate(grabber.getSampleRate());
		audioFileInfo.setDuration(grabber.getFormatContext().duration());
		String extension = FilenameUtils.getExtension(audioFilePath);
		if (!AudioCodeEnum.MP3.getDesc().equalsIgnoreCase(extension)) {
			log.warn("{}文件获取的文件时长不准确，请以mp3文件时长为准", extension);
		}
		close(grabber);
		return audioFileInfo;
	}

	@Override
	public AudioFileInfo getAudioFileInfo(File audioFile) {
		if (!FileUtil.exist(audioFile)) {
			log.error("【javacv】输入文件不存在");
			return null;
		}
		return getAudioFileInfo(FileUtil.getAbsolutePath(audioFile));
	}

	private boolean start(FrameGrabber grabber) {
		try {
			grabber.start();
			return true;
		} catch (org.bytedeco.javacv.FrameGrabber.Exception e2) {
			try {
				log.error("【javacv】首次打开抓取器失败，准备重启抓取器...", e2);
				grabber.restart();
				return true;
			} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
				try {
					log.error("【javacv】重启抓取器失败，正在关闭抓取器...", e);
					grabber.stop();
				} catch (org.bytedeco.javacv.FrameGrabber.Exception e1) {
					log.error("【javacv】停止抓取器失败！", e1);
				}
			}

		}
		return false;
	}

	private boolean start(FrameRecorder recorder) {
		try {
			recorder.start();
			return true;
		} catch (Exception e2) {
			try {
				log.error("【javacv】首次打开录制器失败！准备重启录制器...", e2);
				recorder.stop();
				recorder.start();
				return true;
			} catch (Exception e) {
				try {
					log.error("【javacv】重启录制器失败！正在停止录制器...", e);
					recorder.stop();
				} catch (Exception e1) {
					log.error("【javacv】关闭录制器失败！", e1);
				}
			}
		}
		return false;
	}

	private boolean close(FrameGrabber grabber) {
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
				log.error("【javacv】关闭抓取器失败", e);
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

	@Override
	public void getAudioFileDuration(String audioFilePath, Callback<Long> callback) {
		if (!FileUtil.exist(audioFilePath)) {
			log.error("【javacv】文件:{}不存在", audioFilePath);
			return;
		}
		AudioFileInfo audioFileInfo = getAudioFileInfo(audioFilePath);
		if (AudioCodeEnum.MP3.getDesc().equalsIgnoreCase(audioFileInfo.getCodecName())) {
			log.warn("【javacv】mp3文件时长获取推荐使用getFileInfo()方法");
			callback.doCallback(audioFileInfo.getDuration());
			return;
		}
		StringBuilder outputStr = new StringBuilder(FilenameUtils.getFullPath(audioFilePath));
		outputStr.append(FilenameUtils.getBaseName(audioFilePath)).append(".").append(AudioCodeEnum.MP3.getDesc());
		workerThreadPool.execConvertTask(new AudioConvertWorker(audioFilePath, outputStr.toString(), AudioCodeEnum.MP3,
				null, null, null, 2, callback, callback));
	}

	@Override
	public void getAudioFileDuration(File audioFile, Callback<Long> callback) {
		if (!FileUtil.exist(audioFile)) {
			log.error("【javacv】输入文件不存在");
			return;
		}
		getAudioFileDuration(FileUtil.getAbsolutePath(audioFile), callback);
	}
	
	@Override
	public Long getAudioFileDuration(String audioFilePath) {
		if (!FileUtil.exist(audioFilePath)) {
			log.error("【javacv】文件:{}不存在", audioFilePath);
			return null;
		}
		AudioFileInfo audioFileInfo = getAudioFileInfo(audioFilePath);
		if (AudioCodeEnum.MP3.getDesc().equalsIgnoreCase(audioFileInfo.getCodecName())) {
			return audioFileInfo.getDuration();
		}
		File convert = convert(audioFilePath, AudioCodeEnum.MP3);
		if(convert == null) {
			return null;
		}
		return getAudioFileInfo(convert).getDuration();
	}

	@Override
	public Long getAudioFileDuration(File audioFile) {
		if (!FileUtil.exist(audioFile)) {
			log.error("【javacv】输入文件不存在");
			return null;
		}
		return getAudioFileDuration(FileUtil.getAbsolutePath(audioFile));
	}

	@Override
	public File convert(String inputFilePath, AudioCodeEnum audioCodec) {
		return convert(inputFilePath, audioCodec, null);
	}

	@Override
	public File convert(File inputFile, AudioCodeEnum audioCodec) {
		return convert(inputFile, audioCodec, null);
	}

	@Override
	public File convert(String inputFilePath, AudioCodeEnum audioCodec, Integer audioBitrate) {
		return convert(inputFilePath, audioCodec, audioBitrate, null, null);
	}

	@Override
	public File convert(File inputFile, AudioCodeEnum audioCodec, Integer audioBitrate) {
		return convert(inputFile, audioCodec, audioBitrate, null, null);
	}

	@Override
	public File convert(String inputFilePath, AudioCodeEnum audioCodec, Integer sampleRate, Integer audioBitrate,
			Integer audioChannels) {
		if (!FileUtil.exist(inputFilePath)) {
			log.error("【javacv】文件:{}不存在", inputFilePath);
			return null;
		}
		StringBuilder outputFilePath = new StringBuilder(FilenameUtils.getFullPath(inputFilePath));
		if(audioCodec.getDesc().equalsIgnoreCase(FilenameUtils.getExtension(inputFilePath))) {
			outputFilePath.append("convert").append(File.separatorChar);
		}
		outputFilePath.append(FilenameUtils.getBaseName(inputFilePath)).append(".").append(audioCodec.getDesc());
		return getConvertFile(inputFilePath, outputFilePath.toString(), audioCodec, sampleRate, audioBitrate, audioChannels);
	}

	@Override
	public File convert(File inputFile, AudioCodeEnum audioCodec, Integer sampleRate, Integer audioBitrate,
			Integer audioChannels) {
		if (!FileUtil.exist(inputFile)) {
			log.error("【javacv】输入文件不存在");
			return null;
		}
		return convert(FileUtil.getAbsolutePath(inputFile), audioCodec, sampleRate, audioBitrate, audioChannels);
	}

	@Override
	public void convert(String inputFilePath, String outputFilePath, AudioCodeEnum audioCodec) {
		convert(inputFilePath, outputFilePath, audioCodec, null);
	}

	@Override
	public void convert(File inputFile, String outputFilePath, AudioCodeEnum audioCodec) {
		convert(inputFile, outputFilePath, audioCodec, null);
	}

	@Override
	public void convert(String inputFilePath, String outputFilePath, AudioCodeEnum audioCodec, Integer audioBitrate) {
		convert(inputFilePath, outputFilePath, audioCodec, null, audioBitrate, null);
	}

	@Override
	public void convert(File inputFile, String outputFilePath, AudioCodeEnum audioCodec, Integer audioBitrate) {
		convert(inputFile, outputFilePath, audioCodec, null, audioBitrate, null);
	}

	@Override
	public void convert(String inputFilePath, String outputFilePath, AudioCodeEnum audioCodec, Integer sampleRate,
			Integer audioBitrate, Integer audioChannels) {
		if (!FileUtil.exist(inputFilePath)) {
			log.error("【javacv】文件:{}不存在", inputFilePath);
			return;
		}
		getConvertFile(inputFilePath, outputFilePath, audioCodec, sampleRate, audioBitrate, audioChannels);
	}

	@Override
	public void convert(File inputFile, String outputFilePath, AudioCodeEnum audioCodec, Integer sampleRate,
			Integer audioBitrate, Integer audioChannels) {
		if (!FileUtil.exist(inputFile)) {
			log.error("【javacv】输入文件不存在");
			return;
		}
		getConvertFile(FileUtil.getAbsolutePath(inputFile), outputFilePath, audioCodec, sampleRate, audioBitrate,
				audioChannels);
	}

	@Override
	public void syncConvert(String inputFilePath, String outputFilePath, Callback successCall, Callback failCall) {
		String extension = FilenameUtils.getExtension(outputFilePath);
		syncConvert(inputFilePath, outputFilePath, AudioCodeEnum.getByDesc(extension), successCall, failCall);
	}

	@Override
	public void syncConvert(File inputFile, File outputFile, Callback successCall, Callback failCall) {
		String extension = FilenameUtils.getExtension(outputFile.getName());
		syncConvert(inputFile, outputFile, AudioCodeEnum.getByDesc(extension), successCall, failCall);
	}

	@Override
	public void syncConvert(File inputFile, File outputFile, AudioCodeEnum audioCodec, Callback successCall,
			Callback failCall) {
		syncConvert(inputFile, outputFile, audioCodec, null, successCall, failCall);
	}

	@Override
	public void syncConvert(String inputFilePath, String outputFilePath, AudioCodeEnum audioCodec, Callback successCall,
			Callback failCall) {
		syncConvert(inputFilePath, outputFilePath, audioCodec, null, successCall, failCall);
	}

	@Override
	public void syncConvert(String inputFilePath, String outputFilePath, AudioCodeEnum audioCodec, Integer audioBitrate,
			Callback successCall, Callback failCall) {
		syncConvert(inputFilePath, outputFilePath, audioCodec, null, audioBitrate, null, successCall, failCall);
	}

	@Override
	public void syncConvert(File inputFile, File outputFile, AudioCodeEnum audioCodec, Integer audioBitrate,
			Callback successCall, Callback failCall) {
		syncConvert(inputFile, outputFile, audioCodec, null, audioBitrate, null, successCall, failCall);
	}

	@Override
	public void syncConvert(String inputFilePath, String outputFilePath, AudioCodeEnum audioCodec, Integer sampleRate,
			Integer audioBitrate, Integer audioChannels, Callback successCall, Callback failCall) {
		if (!FileUtil.exist(inputFilePath)) {
			log.error("【javacv】文件:{}不存在", inputFilePath);
			return;
		}
		if(audioCodec.getDesc().equalsIgnoreCase(FilenameUtils.getExtension(inputFilePath))) {
			log.error("【javacv】输出文件全路径和输入文件全路径存在冲突");
			return;
		}
		FileUtil.mkParentDirs(new File(outputFilePath));
		workerThreadPool.execConvertTask(new AudioConvertWorker(inputFilePath, outputFilePath, audioCodec, sampleRate,
				audioBitrate, audioChannels, 1, successCall, failCall));
	}

	@Override
	public void syncConvert(File inputFile, File outputFile, AudioCodeEnum audioCodec, Integer sampleRate,
			Integer audioBitrate, Integer audioChannels, Callback successCall, Callback failCall) {
		if(!FileUtil.exist(inputFile) || outputFile == null) {
			return;
		}
		syncConvert(FileUtil.getAbsolutePath(inputFile), FileUtil.getAbsolutePath(outputFile), audioCodec, sampleRate,
				audioBitrate, audioChannels, successCall, failCall);
	}
	
}