package com.liuhy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.liuhy.service.AudioConvertService;
import com.liuhy.service.AudioPushService;
import com.liuhy.service.impl.AudioConvertServiceImpl;
import com.liuhy.service.impl.AudioPushServiceImpl;
import com.liuhy.worker.AudioWorkerThreadPool;

/**
* springboot配置
* @author liuhy
* @version 1.0
*/
@Configuration
@EnableConfigurationProperties(value = {JavaCVEnableProperties.class,JavaCVAudioProperties.class,JavaCVThreadPoolProperties.class})
@ConditionalOnClass(AudioPushService.class)// 当PersonService这个类在类路径中时，且当前容器中没有这个Bean的情况下，开始自动配置
@ConditionalOnProperty(prefix = "javacv", value = "enabled", matchIfMissing = true)
public class JavaCVAutoConfiguration {

    @Autowired
    private JavaCVAudioProperties javaCVProperties;
    
    @Autowired
    private JavaCVThreadPoolProperties javaCVThreadPoolProperties;
    
    @Autowired
    private AudioWorkerThreadPool audioWorkerThreadPool;

    @Bean
    @ConditionalOnMissingBean(AudioWorkerThreadPool.class)// 当容器中没有指定Bean的情况下，自动配置PersonService类
    public AudioWorkerThreadPool audioWorkerThreadPoolConfiguration() {
    	return new AudioWorkerThreadPool(javaCVThreadPoolProperties);
    }
    
    @Bean
    @ConditionalOnMissingBean(AudioPushService.class)// 当容器中没有指定Bean的情况下，自动配置PersonService类
    public AudioPushService audioPushServiceConfiguration() {
    	return new AudioPushServiceImpl(javaCVProperties, audioWorkerThreadPool);
    }
    
    @Bean
    @ConditionalOnMissingBean(AudioConvertService.class)// 当容器中没有指定Bean的情况下，自动配置PersonService类
    public AudioConvertService audioConvertServiceConfiguration() {
    	return new AudioConvertServiceImpl(audioWorkerThreadPool);
    }
}

