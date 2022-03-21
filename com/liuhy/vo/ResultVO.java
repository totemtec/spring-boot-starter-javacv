package com.liuhy.vo;

import java.io.Serializable;

import lombok.Data;

/**
* 返回结果类
* @author liuhy
* @version 1.0
*/
@Data
public class ResultVO<T> implements Serializable {


    private static final long serialVersionUID = 4330810419929109013L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 返回结果
     */
    private T data;
}
