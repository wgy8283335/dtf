package com.coconason.dtf.manager.message;

import java.io.IOException;
import java.io.Serializable;

/**
 * Message information.
 *
 * @Author: Jason
 */
public interface MessageInfoInterface extends Serializable {
    
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
    
    /**
     * Get time stamp of group.
     * @return time stamp
     */
    long getTimeStamp();

    /**
     * Get member information in byte[].
     * 
     * @return
     */
    byte[] toBytes() throws IOException;
    
    /**
     * Get position in log.
     * @return time stamp
     */
    int getPosition();
    
    /**
     * Set position in log.
     * @return time stamp
     */
    void setPosition(int position);
    
}
