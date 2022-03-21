package com.liuhy.service;

import lombok.Data;

/**
* 回调类
* @author liuhy
* @version 1.0
*/
@Data
public abstract class Callback<E> {
	
	private E param;
	
	public Callback(E e) {
		this.param = e;
	}
	public Callback() {
		
	}
	
    /**
     * @param e 参数
     */
    public abstract void doCallback(E e);
}  
