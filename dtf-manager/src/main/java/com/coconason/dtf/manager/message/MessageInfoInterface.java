package com.coconason.dtf.manager.message;

/**
 * Message information.
 *
 * @Author: Jason
 */
public interface MessageInfoInterface {
    
    /**
     * Get member id of group.
     * @return member id
     */
    String getGroupMemberId();
    
    /**
     * Check whether is committed.
     * @return Whether is committed
     */
    Boolean isCommitted();
    
    /**
     * Get url of request.
     *
     * @return url of request
     */
    String getUrl();
    
    /**
     * Get parameter of request.
     *
     * @return parameter of request
     */
    Object getObj();
    
    /**
     * Set the value of isCommitted.
     *
     * @param committed boolean
     */
    void setCommitted(boolean committed);
}
