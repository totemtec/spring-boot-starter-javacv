package com.liuhy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
* 线程池配置
* @author liuhy
* @version 1.0
*/
@ConfigurationProperties(prefix = "javacv.audio.threadpool")
public class JavaCVThreadPoolProperties {
	
	private Integer corePoolSize = 2;
    
	private Integer maximumPoolSize = 10;
    
    private Long keepAliveTime = 200000L;

	public Integer getCorePoolSize() {
		return corePoolSize;
	}

	public void setCorePoolSize(Integer corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public Integer getMaximumPoolSize() {
		return maximumPoolSize;
	}

	public void setMaximumPoolSize(Integer maximumPoolSize) {
		this.maximumPoolSize = maximumPoolSize;
	}

	public Long getKeepAliveTime() {
		return keepAliveTime;
	}

	public void setKeepAliveTime(Long keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
	}
    

}

