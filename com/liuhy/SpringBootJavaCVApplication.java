package com.liuhy;

import javax.annotation.PreDestroy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.liuhy.cache.CacheUtil;

import lombok.extern.slf4j.Slf4j;

/**
* 启动类
* @author liuhy
* @version 1.0
*/
@SpringBootApplication
@Slf4j
public class SpringBootJavaCVApplication {

    public static void main(String[] args) {
        // 将服务启动时间存入缓存
        CacheUtil.STARTTIME = System.currentTimeMillis();
        SpringApplication.run(SpringBootJavaCVApplication.class, args);
    }

    @PreDestroy
    public void destory() {
        log.info("【javacv】服务结束，开始释放空间...");
        // 关闭线程池
    }
}
