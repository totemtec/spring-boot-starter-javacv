package com.liuhy.enums;

import lombok.Getter;

/**
* 返回结果枚举
* @author liuhy
* @version 1.0
*/
@Getter
public enum ResultEnum implements CodeEnum {
    /**
     * 操作成功
     */
    SUCCESS(0, "成功"),
    /**
     * 操作失败
     */
    ERROR(2, "失败"),
    /**
     * 参数错误
     */
    PARAMES_ERROR(3, "参数错误"),
    /**
     * 服务错误
     */
    SERVICE_ERROR(4,"服务错误"),

    ;


    private Integer code;

    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
