package com.coconason.dtf.manager.message;

import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * Adaptor of transaction message group interface for submitting.
 * 
 * @Author: Jason
 */
public abstract class TransactionMessageForSubmitAdaptor implements TransactionMessageGroupInterface<String> {
    
    /**
     * Override method.
     */
    @Override
    public void addMember(final String memberId, final String url, final Object obj, final String httpAction) {
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
     *
     * @return null
     */
    @Override
    public Boolean isCommitted() {
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
    public List<TransactionMessageForAddingInterface> getMemberList() {
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
