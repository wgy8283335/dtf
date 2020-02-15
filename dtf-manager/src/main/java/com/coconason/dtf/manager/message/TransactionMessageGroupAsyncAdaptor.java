package com.coconason.dtf.manager.message;

import io.netty.channel.ChannelHandlerContext;

/**
 * Adaptor of transaction message group interface for asynchronous.
 *
 * @Author: Jason
 */
public abstract class TransactionMessageGroupAsyncAdaptor implements TransactionMessageGroupInterface<MessageInfoInterface> {
    
    /**
     * Override method.
     */
    @Override
    public void addMemberToGroup(final TransactionMessageForAddingInterface e) {
    }
        
    /**
     * Override method.
     */
    @Override
    public void setCtxForSubmitting(final ChannelHandlerContext ctxForSubmitting) {
    }
    
    /**
     * Override method.
     *
     * @return null
     */
    @Override
    public ChannelHandlerContext getCtx() {
        return null;
    }
    
    /**
     * Override method.
     *
     * @return null
     */
    @Override
    public String getGroupMemberId() {
        return null;
    }
    
    /**
     * Override method.
     */
    @Override
    public void setCommitted(final boolean commited) {
    }
    
    /**
     * Override method.
     */
    @Override
    public void setGroupId(final String groupId) {
    }
    
    /**
     * Override method.
     * 
     * @return null
     */
    @Override
    public Boolean isCommitted() {
        return null;
    }
    
    /**
     * Override method.
     *
     * @return null
     */
    @Override
    public String getUrl() {
        return null;
    }
    
    /**
     * Override method.
     *
     * @return null
     */
    @Override
    public Object getObj() {
        return null;
    }
    
}
