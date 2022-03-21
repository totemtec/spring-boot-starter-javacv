package com.liuhy.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
/**
* 保存流为文件请求
* @author liuhy
* @version 1.1
*/
@Data
public class StreamToFileRequest  implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 82831177007975814L;

	/**
     * 任务id，需要保持唯一性 -必填
     */
    @JsonProperty("id")
    private String id = "";
    
    /**
     * 源流地址支持：rtp,rtsp，rtmp,udp
     * 
     */
    @JsonProperty("srcStreamUrl")
    private String srcStreamUrl;

    /**
     * 目的文件的绝对路径-必填
     */
    @JsonProperty("destFilePath")
    private String destFilePath;
    
    /**
     * 是否循环推流，默认：否 -选填
     */
    @JsonProperty("isLoop")
    private Boolean isLoop=false;
    
    /**
     * 推流结束时间 -选填
     */
    @JsonProperty("cancelTime")
    private Date cancelTime;
    
    /**
     * 帧率 -选填
     */
    @JsonProperty("frameRate")
    private Integer frameRate;
    
    /**
     * 通道数
     */
    @JsonProperty("channels")
    private Integer channels;

    /**
     * 采样率 -选填
     */
    @JsonProperty("sampleRate")
    private Integer sampleRate;
    
    /**
     * 比特率 -选填
     */
    @JsonProperty("bitRate")
    private Integer bitRate;
    
    /**
     * 文件编码 -选填
     */
    @JsonProperty("codecName")
    private String codecName;
}
