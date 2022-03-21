package com.liuhy.model;

import java.io.Serializable;

import com.liuhy.service.Callback;

import lombok.Data;

/**
* 推流任务类
* @author liuhy
* @version 1.0
*/
@Data
public class AudioPushTask implements Serializable {

    private static final long serialVersionUID = 1095351988501724233L;

    /**
     * 推流参数
     */
    private AudioPushRequest audioPushRequest;
    
    /**
     * 开始回调
     */
    private Callback startCallback;
    
    /**
     * 结束回调
     */
    private Callback endCallback;
}
