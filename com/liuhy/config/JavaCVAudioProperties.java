package com.liuhy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
* javacv参数配置
* @author liuhy
* @version 1.0
*/
@ConfigurationProperties(prefix = "javacv.audio.pusher")
@Data
public class JavaCVAudioProperties {
	
    private Boolean isLoop = false;
    
    private Integer frameRate = 24;
    
    private Integer channels;
    
    private Integer bitRate;
    
    private Integer sampleRate;
    
    private String codecName;
}

