package com.liuhy.handler;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.liuhy.exceptions.PusherException;
import com.liuhy.util.ResultUtil;
import com.liuhy.vo.ResultVO;

/**
* 异常处理类
* @author liuhy
* @version 1.0
*/
@ControllerAdvice
public class PusherExceptionHandler {

    @ExceptionHandler(value = PusherException.class)
    @ResponseBody
    public ResultVO handlerPusherException(PusherException e){
        return ResultUtil.error(e.getCode(),e.getMessage());
    }

}
