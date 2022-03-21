package com.liuhy.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
/**
* 音频文件信息
* @author liuhy
* @version 1.0
*/
@Data
public class AudioFileInfo  implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7565119460655592565L;

	/**
     * 通道数
     */
    @JsonProperty("channels")
    private Integer channels;

    /**
     * 采样率
     */
    @JsonProperty("sampleRate")
    private Integer sampleRate;
    /**
     * 比特率
     */
    @JsonProperty("bitRate")
    private Integer bitRate;
    /**
     * 文件编码
     */
    @JsonProperty("codecName")
    private String codecName;
    /**
     * 文件时长（毫秒）
     */
    @JsonProperty("duration")
    private Long duration;
}
