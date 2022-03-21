package com.liuhy.exceptions;

import com.liuhy.enums.ResultEnum;

import lombok.Data;

/**
* 推流异常类
* @author liuhy
* @version 1.0
*/
@Data
public class PusherException extends RuntimeException {

    private Integer code;

    public PusherException(ResultEnum resultEnum){
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
    }

    public PusherException(String message){
        super(message);
    }

    public PusherException(Integer code, String message){
        super(message);
        this.code = code;
    }

}
