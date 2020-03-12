package com.coconason.dtf.manager.message;

/**
 * Message information.
 * 
 * @Author: Jason
 */
public final class MessageInfo implements MessageInfoInterface {
    
    private static final long serialVersionUID = -1L;
    
    //?需要添加http动作作为属性
    /**
     * HTTP Action.
     */
    private String httpAction;
    
    /**
     * Member id.
     */
    private String memberId;
    
    /**
     * Url of request.
     */
    private String url;
    
    /**
     * Parameter of request.
     */
    private String obj;
    
    /**
     * Time stamp of request.
     */
    private long timeStamp;
    
    /**
     * Whether is committed.
     */
    private boolean isCommitted;
    
    /**
     * Position in log.
     */
    private int position;
    
    public MessageInfo(final String memberId, final boolean isCommitted, final String url, final Object obj, final long timeStamp, final String httpAction) {
        this.memberId = memberId;
        this.isCommitted = isCommitted;
        this.url = url;
        this.obj = obj.toString();
        this.timeStamp = timeStamp;
        this.httpAction = httpAction;
    }
    
    /**
     * Get http action.
     * @return http action
     */
    public String getHttpAction(){
        return httpAction;
    }
    
    /**
     * Get time stamp of group.
     * @return time stamp
     */
    @Override
    public long getTimeStamp() {
        return timeStamp;
    }
    
    /**
     * Set position in log.
     */
    @Override
    public void setPosition(final int position) {
        this.position = position;
    }
    
    /**
     * Get position in log.
     * @return position
     */
    @Override
    public int getPosition() {
        return position;
    }
    
    /**
     * Get member id of group.
     * @return member id
     */
    @Override
    public String getGroupMemberId() {
        return memberId;
    }
    
    /**
     * Check whether is committed.
     * @return Whether is committed
     */
    @Override
    public Boolean isCommitted() {
        return isCommitted;
    }
    
    /**
     * Get url of request.
     * 
     * @return url of request
     */
    @Override
    public String getUrl() {
        return url;
    }
    
    /**
     * Get parameter of request.
     * 
     * @return parameter of request
     */
    @Override
    public String getObj() {
        return obj;
    }

    /**
     * Set the value of isCommitted.
     * 
     * @param committed boolean
     */
    @Override
    public void setCommitted(final boolean committed) {
        isCommitted = committed;
    }

    /**
     * Override equals method.
     * 
     * @param o MessageInfo object
     * @return true or false
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MessageInfo that = (MessageInfo) o;
        if (isCommitted != that.isCommitted) {
            return false;
        }
        if (!memberId.equals(that.memberId)) {
            return false;
        }
        if (!getUrl().equals(that.getUrl())) {
            return false;
        }
        return getObj().equals(that.getObj());
    }
    
    /**
     * Override hashCode method.
     * 
     * @return hash code
     */
    @Override
    public int hashCode() {
        int result = memberId.hashCode();
        result = 31 * result + (isCommitted ? 1 : 0);
        result = 31 * result + getUrl().hashCode();
        result = 31 * result + getObj().hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return "{" 
                + "memberId:" + memberId
                + ", url:" + url
                + ", obj:" + obj
                + ", timeStamp:" + timeStamp
                + ", isCommitted:" + isCommitted
                + "}";
    }
    
}
