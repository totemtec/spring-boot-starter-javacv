package com.liuhy.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
/**
* 文件推流请求
* @author liuhy
* @version 1.1
*/
@Data
public class FileToStreamRequest  implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6670941077710806105L;

	/**
     * 任务id，需要保持唯一性 -必填
     */
    @JsonProperty("id")
    private String id = "";
    
    /**
     * 推流源地址 -必填
     * 文件地址支持:网络地址（http/https）或者文件本地路径；
     * 
     */
    @JsonProperty("srcFileUrlList")
    private List<String> srcFileUrlList;

    /**
     * 推流目的地址-必填
     * 支持rtmp地址、rtsp地址，rtp地址，udp地址
     */
    @JsonProperty("destStreamUrl")
    private String destStreamUrl;
    
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
