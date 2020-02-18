package com.coconason.dtf.manager.message;

import io.netty.channel.ChannelHandlerContext;

/**
 * Transaction message for adding.
 *
 * @Author: Jason
 */
public interface TransactionMessageForAddingInterface {
    
    /**
     * Get group member id.
     *
     * @return group member id
     */
    String getGroupMemberId();
    
    /**
     * Get context.
     *
     * @return channel handler context
     */
    ChannelHandlerContext getCtx();
    
    /**
     * Whether is committed.
     *
     * @return true or false
     */
    Boolean isCommitted();
    
    /**
     * Set committed value.
     *
     * @param commited boolean
     */
    void setCommitted(boolean commited);
    
    /**
     * Override toString method.
     *
     * @return string
     */
    String toString();
}
