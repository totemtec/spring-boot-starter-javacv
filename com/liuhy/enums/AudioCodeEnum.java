package com.liuhy.enums;

import lombok.Getter;

/**
* 音频文件编码
* @author liuhy
* @version 1.0
*/
@Getter
public enum AudioCodeEnum implements CodeEnum {
    /**
     * mp3格式音频
     */
    MP3(86017, "mp3"),
    /**
     * aac格式音频
     */
    AAC(86018, "aac"),
    /**
     * wav格式音频
     */
    WAV(65536, "wav"),

    ;

    private Integer code;

    private String desc;

    AudioCodeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static int getCodeByDesc(String fileExtName){
    	for (AudioCodeEnum ace : AudioCodeEnum.values()) {
			if(ace.getDesc().equalsIgnoreCase(fileExtName)) {
				return ace.getCode();
			}
		}
    	return 0;
    }
    
    public static String getDescByCode(Integer fileCode){
    	for (AudioCodeEnum ace : AudioCodeEnum.values()) {
			if(ace.getCode().equals(fileCode)) {
				return ace.getDesc();
			}
		}
    	return null;
    }
    
    public static AudioCodeEnum getByDesc(String fileExtName){
    	for (AudioCodeEnum ace : AudioCodeEnum.values()) {
			if(ace.getDesc().equalsIgnoreCase(fileExtName)) {
				return ace;
			}
		}
    	return null;
    }

}
