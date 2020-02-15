package com.coconason.dtf.manager.message;

/**
 * Message information.
 * 
 * @Author: Jason
 */
public final class MessageInfo implements MessageInfoInterface {
    
    /**
     * Member id.
     */
    private String memberId;

    /**
     * Whether is committed.
     */
    private boolean isCommitted;
    
    /**
     * Url of request.
     */
    private String url;
    
    /**
     * parameter of request.
     */
    private Object obj;

    public MessageInfo(final String memberId, final boolean isCommitted, final String url, final Object obj) {
        this.memberId = memberId;
        this.isCommitted = isCommitted;
        this.url = url;
        this.obj = obj;
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
    public Object getObj() {
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
     * Over ride equals method.
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
     * Over ride hashCode method.
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
}
