package com.liuhy.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;
/**
* 推流扩展参数
* @author liuhy
* @version 1.0
*/
@Data
@ToString
public class AudioPushRequest  implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4505606606247421869L;

	/**
     * PushTask的id，需要保持唯一性 -必填
     */
    @JsonProperty("id")
    private String id = "";
    
    /**
     * 推流源地址 -必填
     * 1.文件地址支持:网络地址（http/https）或者文件本地路径；
     * 2.流地址支持：rtp,rtsp，rtmp
     * 
     */
    @JsonProperty("pushSrcUrlList")
    private List<String> pushSrcUrlList;

    /**
     * 推流目的地址-必填
     * 支持rtmp地址、rtsp地址，rtp地址
     */
    @JsonProperty("pushDescUrl")
    private String pushDestUrl = "";
    
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
