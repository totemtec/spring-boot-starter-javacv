package com.liuhy.enums;

import lombok.Getter;

/**
* 任务状态
* @author liuhy
* @version 1.0
*/
@Getter
public enum WorkStatusEnum {
    /**
     * 运行中
     */
    RUNNING(0, "running"),
    /**
     * 不存在
     */
    NOTEXIST(1, "not existing"),
    /**
     * 已停止
     */
    STOPPED(2, "stopped"),
    /**
     * 等待启动中
     */
    WAITING(3, "waiting"),
	
    /**
     * 参数错误
     */
	PARAMSERRROR(4, "params error");

    private Integer status;

    private String msg;

    WorkStatusEnum(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

}
