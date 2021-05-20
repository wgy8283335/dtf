package com.dtf.demo3.constant;

public enum ErrorCode {

    GOODS_NOT_ENOUGH(100003),

    RECORD_NOT_EXIST_ERROR(100002),

    PARAM_ERROR(100001),

    SYS_ERROR(100000),

    OK(200);

    private final Integer value;

    ErrorCode(final Integer value) {
        this.value = value;
    }

    /**
     * Return the integer value of this status code.
     *
     * @return value
     */
    public Integer value() {
        return this.value;
    }

    /**
     * Return value in String.
     *
     * @return value in String.
     */
    public String toString() {
        return this.value.toString();
    }

    /**
     * Return message according to value.
     *
     * @return message
     */
    public String msg() {
        if (this.value == 200) {
            return "success";
        } else {
            return "HTTPCODE_" + this.value;
        }
    }

}
