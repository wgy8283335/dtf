package com.coconason.dtf.manager.message;

/**
 * @Author: Jason
 * @date: 2018/9/6-13:14
 */
public class MessageInfo extends MessageInfoAdaptor{
    private String memberId;
    private boolean isCommitted;
    private String url;
    private Object obj;

    public MessageInfo(String memberId, boolean isCommitted, String url, Object obj) {
        this.memberId = memberId;
        this.isCommitted = isCommitted;
        this.url = url;
        this.obj = obj;
    }
    @Override
    public String getGroupMemberId() {
        return memberId;
    }
    @Override
    public Boolean isCommitted() {
        return isCommitted;
    }
    @Override
    public String getUrl() {
        return url;
    }
    @Override
    public Object getObj() {
        return obj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){ return true;}
        if (o == null || getClass() != o.getClass()) {return false;}

        MessageInfo that = (MessageInfo) o;

        if (isCommitted != that.isCommitted) {return false;}
        if (!memberId.equals(that.memberId)) {return false;}
        if (!getUrl().equals(that.getUrl())) {return false;}
        return getObj().equals(that.getObj());
    }

    @Override
    public int hashCode() {
        int result = memberId.hashCode();
        result = 31 * result + (isCommitted ? 1 : 0);
        result = 31 * result + getUrl().hashCode();
        result = 31 * result + getObj().hashCode();
        return result;
    }
}
