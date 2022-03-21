package com.liuhy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
/**
* javacv是否启用
* @author liuhy
* @version 1.0
*/
@ConfigurationProperties(prefix = "javacv")
public class JavaCVEnableProperties {

	private Boolean enabled = false;

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	
}

