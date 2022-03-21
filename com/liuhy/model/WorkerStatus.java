package com.liuhy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.liuhy.enums.WorkStatusEnum;

import cn.hutool.core.util.EnumUtil;
import lombok.Data;

/**
* 枚举类
* @author liuhy
* @version 1.0
*/
@Data
public class WorkerStatus {

    /**
     * 当前推流worker的工作时长
     */
    private Integer workerStatus = WorkStatusEnum.NOTEXIST.getStatus();

    /**
     * 当前推流worker工作时长
     */
    private long runningTime = 0L;

    @JsonIgnore
    WorkStatusEnum getWorkStatusEnum() {
        return EnumUtil.getEnumAt(WorkStatusEnum.class, workerStatus);
    }
    
    public WorkerStatus(WorkStatusEnum ws) {
    	this.workerStatus = ws.getStatus();
    }
    
    public WorkerStatus() {
    }
    
    public static WorkerStatus builer(WorkStatusEnum ws) {
    	return new WorkerStatus(ws);
    }

}
