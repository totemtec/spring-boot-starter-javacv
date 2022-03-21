package com.liuhy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.liuhy.enums.ResultEnum;
import com.liuhy.exceptions.PusherException;
import com.liuhy.model.FileToStreamRequest;
import com.liuhy.model.WorkerStatus;
import com.liuhy.service.AudioPushService;
import com.liuhy.util.ResultUtil;
import com.liuhy.vo.ResultVO;

import cn.hutool.core.util.StrUtil;

/**
* 推流控制器
* @author liuhy
* @version 1.0
*/
@RestController
public class PushController {

    @Autowired
    private AudioPushService pushService;

    /**
     * @param fileToStreamRequest 参数
     * @return ResultVO 结果
     */
    @PostMapping(value = "/pushers")
    public ResultVO<WorkerStatus> pusher(@RequestBody FileToStreamRequest fileToStreamRequest) {
        if (StrUtil.hasBlank(fileToStreamRequest.getDestStreamUrl())) {
            throw new PusherException(ResultEnum.PARAMES_ERROR);
        }
        WorkerStatus status = pushService.createFileToStreamTask(fileToStreamRequest);
        return ResultUtil.success(status);
    }

    /**
     * 获取指定pusher的工作状态
     * @param id 任务id
     * @return ResultVO 结果
     */
    @GetMapping(value = "/pushers/status")
    public ResultVO<WorkerStatus> pusherStatus(@RequestParam("id") String id ){
        if(StrUtil.isBlank(id)){
            throw new PusherException(ResultEnum.PARAMES_ERROR);
        }
        WorkerStatus status = pushService.fileToStreamTaskStatus(id);
        return ResultUtil.success(status);
    }

    /**
     * 停止指定pusher的工作
     * @param id 任务id
     * @return ResultVO 结果
     */
    @DeleteMapping(value = "/pushers")
    public ResultVO<WorkerStatus> stopPusher(@RequestParam("id") String id){
        if(StrUtil.isBlank(id)){
            throw new PusherException(ResultEnum.PARAMES_ERROR);
        }
        WorkerStatus status = pushService.stopFileToStreamTask(id);
        return ResultUtil.success(status);
    }

}
