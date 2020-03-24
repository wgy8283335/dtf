package com.coconason.dtf.demo3.model;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;


public class DemoResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    // 响应业务状态
    private Integer code;
    // 响应消息
    private String message;
    // 响应中的数据
    private T datum;

    public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

    public T getDatum() {
        return datum;
    }

    public void setDatum(T datum) {
        this.datum = datum;
    }

    public DemoResult(){
        this.code = 200;
        this.message = "OK";
        this.datum = null;
    }

    public DemoResult(Integer code, String message, T datum) {
        this.code = code;
        this.message = message;
        this.datum = datum;
    }

    public DemoResult(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.datum = null;
    }

    public DemoResult(T datum) {
        this.code = 200;
        this.message = "OK";
        this.datum = datum;
    }
    
    public DemoResult(String information) {
        JSONObject map = new JSONObject().parseObject(information);
        code = map.getInteger("code");
        message = map.getString("message");
    }
    
    public DemoResult build(Integer code, String message, T datum) {
        return new DemoResult(code, message, datum);
    }
    public DemoResult build(Integer code, String message) {
        return new DemoResult(code, message, null);
    }
    
    public DemoResult ok(T datum) {
        return new DemoResult(datum);
    }

    public DemoResult ok() {
        return new DemoResult();
    }


}
