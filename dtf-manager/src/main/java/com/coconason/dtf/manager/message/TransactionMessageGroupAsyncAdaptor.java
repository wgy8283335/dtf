package com.coconason.dtf.manager.message;

import io.netty.channel.ChannelHandlerContext;

/**
 * @Author: Jason
 * @date: 2018/9/6-11:54
 */
public abstract class TransactionMessageGroupAsyncAdaptor implements TransactionMessageGroupInterface<MessageInfo> {
    @Override
    public void addMemberToGroup(TransactionMessageForAdding e) {

    }

    @Override
    public void setCtxForSubmitting(ChannelHandlerContext ctxForSubmitting) {

    }

    @Override
    public ChannelHandlerContext getCtx() {
        return null;
    }

    @Override
    public String getGroupMemberId(){
        return null;
    }

    @Override
    public Boolean isCommitted(){
        return null;
    }

    @Override
    public void setCommitted(boolean commited){
    }

    @Override
    public void setGroupId(String groupId){

    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public Object getObj() {
        return null;
    }
}
