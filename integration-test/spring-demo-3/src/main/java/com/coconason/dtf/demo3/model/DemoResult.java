package com.coconason.dtf.demo3.model;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class DemoResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer code;

    private String message;

    private T datum;

    public DemoResult() {
        this.code = 200;
        this.message = "OK";
        this.datum = null;
    }

    public DemoResult(final Integer code, final String message, final T datum) {
        this.code = code;
        this.message = message;
        this.datum = datum;
    }

    public DemoResult(final Integer code, final String message) {
        this.code = code;
        this.message = message;
        this.datum = null;
    }

    public DemoResult(final T datum) {
        this.code = 200;
        this.message = "OK";
        this.datum = datum;
    }

    public DemoResult(final String information) {
        JSONObject map = new JSONObject().parseObject(information);
        code = map.getInteger("code");
        message = map.getString("message");
    }

    /**
     * Get code value.
     *
     * @return code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * Set code value.
     *
     * @param code Integer
     */
    public void setCode(final Integer code) {
        this.code = code;
    }

    /**
     * Get message.
     *
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set message.
     *
     * @param message String
     */
    public void setMessage(final String message) {
        this.message = message;
    }

    /**
     * Get datum.
     *
     * @return datum value
     */
    public T getDatum() {
        return datum;
    }

    /**
     * Set datum.
     *
     * @param datum T
     */
    public void setDatum(final T datum) {
        this.datum = datum;
    }

    /**
     * Build DemoResult and return.
     *
     * @param code Integer
     * @param message Integer
     * @param datum T
     * @return DemoResult
     */
    public DemoResult build(final Integer code, final String message, final T datum) {
        return new DemoResult(code, message, datum);
    }

    /**
     * Build DemoResult and return.
     *
     * @param code Integer
     * @param message Integer
     * @return DemoResult
     */
    public DemoResult build(final Integer code, final String message) {
        return new DemoResult(code, message, null);
    }

    /**
     * Build DemoResult and return ok.
     *
     * @param datum T
     * @return DemoResult
     */
    public DemoResult ok(final T datum) {
        return new DemoResult(datum);
    }

    /**
     * Build DemoResult and return ok.
     *
     * @return DemoResult
     */
    public DemoResult ok() {
        return new DemoResult();
    }

}
