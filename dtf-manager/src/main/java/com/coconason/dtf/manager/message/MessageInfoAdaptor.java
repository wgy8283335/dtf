package com.coconason.dtf.manager.message;

import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.Set;

/**
 * @Author: Jason
 * @date: 2018/9/6-11:54
 */
public abstract class MessageInfoAdaptor implements TransactionMessageGroupInterface<MessageInfo> {
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
    public void setCommitted(boolean commited){
    }

    @Override
    public void setGroupId(String groupId){

    }

    @Override
    public void addMember(String memberId, String url, Object obj) {
    }

    @Override
    public String getGroupId() {
        return null;
    }

    @Override
    public List<TransactionMessageForAdding> getMemberList() {
        return null;
    }

    @Override
    public Set<MessageInfo> getMemberSet() {
        return null;
    }
}
