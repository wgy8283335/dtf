package com.coconason.dtf.demo2.message;

/**
 * @Author: Jason
 * @date: 2018/9/6-13:14
 */
public class MessageInfo{
    private String memberId;
    private boolean isSubmitted;
    private String url;
    private Object obj;

    public MessageInfo(String memberId, boolean isSubmitted, String url, Object obj) {
        this.memberId = memberId;
        this.isSubmitted = isSubmitted;
        this.url = url;
        this.obj = obj;
    }

    public String getMemberId() {
        return memberId;
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }

    public String getUrl() {
        return url;
    }

    public Object getObj() {
        return obj;
    }

    public void setSubmitted(boolean submitted) {
        isSubmitted = submitted;
    }
}
