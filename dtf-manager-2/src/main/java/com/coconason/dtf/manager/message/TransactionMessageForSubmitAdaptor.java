package com.coconason.dtf.manager.message;

import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * @Author: Jason
 * @date: 2018/8/24-17:04
 */
public abstract class TransactionMessageForSubmitAdaptor implements TransactionMessageGroupInterface<String> {

    @Override
    public void addMember(String memberId, String url, Object obj) {
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
    public void addMemberToGroup(TransactionMessageForAddingInterface e) {

    }
    @Override
    public void setCtxForSubmitting(ChannelHandlerContext ctxForSubmitting) {

    }
    @Override
    public ChannelHandlerContext getCtx() {
        return null;
    }
    @Override
    public List<TransactionMessageForAddingInterface> getMemberList() {
        return null;
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
