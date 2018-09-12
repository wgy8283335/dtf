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

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        MessageInfo that = (MessageInfo) o;
        if (isSubmitted() != that.isSubmitted()) {return false;}
        if (getMemberId() != null ? !getMemberId().equals(that.getMemberId()) : that.getMemberId() != null)
        {return false;}
        if (getUrl() != null ? !getUrl().equals(that.getUrl()) : that.getUrl() != null) {return false;}
        return getObj() != null ? getObj().equals(that.getObj()) : that.getObj() == null;
    }

    @Override
    public int hashCode() {
        int result = getMemberId() != null ? getMemberId().hashCode() : 0;
        result = 31 * result + (isSubmitted() ? 1 : 0);
        result = 31 * result + (getUrl() != null ? getUrl().hashCode() : 0);
        result = 31 * result + (getObj() != null ? getObj().hashCode() : 0);
        return result;
    }
}
