package com.coconason.dtf.manager.message;

import io.netty.channel.ChannelHandlerContext;

/**
 * Transaction message for adding.
 * 
 * @Author: Jason
 */
public final class TransactionMessageForAdding implements TransactionMessageForAddingInterface {
    
    /**
     * Group member id.
     */
    private String groupMemberId;
    
    /**
     * Channel handler context.
     */
    private ChannelHandlerContext ctx;
    
    /**
     * Method.
     */
    private String method;
    
    /**
     * Parameters of method.
     */
    private String args;
    
    /**
     * Whether is committed.
     * Default value is false.
     */
    private boolean committed;
    
    public TransactionMessageForAdding(final String groupMemberId, final ChannelHandlerContext ctx, final String method, final String args) {
        this.groupMemberId = groupMemberId;
        this.ctx = ctx;
        this.method = method;
        this.args = args;
    }
    
    /**
     * Get group member id.
     * 
     * @return group member id
     */
    @Override
    public String getGroupMemberId() {
        return groupMemberId;
    }
    
    /**
     * Get context.
     * 
     * @return channel handler context
     */
    @Override
    public ChannelHandlerContext getCtx() {
        return ctx;
    }
    
    /**
     * Whether is committed.
     * 
     * @return true or false
     */
    @Override
    public Boolean isCommitted() {
        return committed;
    }
    
    /**
     * Set committed value.
     * 
     * @param commited boolean
     */
    @Override
    public void setCommitted(final boolean commited) {
        this.committed = commited;
    }

    /**
     * Override toString method.
     * 
     * @return string
     */
    @Override
    public String toString() {
        return "groupMemberId='" + groupMemberId + '\''
                + ", ctx=" + ctx
                + ", method='" + method + '\''
                + ", args='" + args + '\''
                + ", commited=" + committed;
    }
    
}
