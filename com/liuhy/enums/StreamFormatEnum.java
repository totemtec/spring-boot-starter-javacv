package com.liuhy.enums;

import lombok.Getter;

/**
* 流格式化
* @author liuhy
* @version 1.0
*/
@Getter
public enum StreamFormatEnum {
    /**
     * rtmp流
     */
	rtmp("flv", "rtmp"),
    /**
     * rtsp流
     */
	rtsp("mpeg-ts", "rtsp"),
    /**
     * rtp流
     */
    rtp("mpeg-ts", "rtp"),
    /**
     * udp流
     */
    udp("mpeg-ts", "udp"),

    ;

    private String code;

    private String name;

    StreamFormatEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public static String getCodeByName(String name){
    	for (StreamFormatEnum ace : StreamFormatEnum.values()) {
			if(ace.getName().equalsIgnoreCase(name)) {
				return ace.getCode();
			}
		}
    	return null;
    }

}
