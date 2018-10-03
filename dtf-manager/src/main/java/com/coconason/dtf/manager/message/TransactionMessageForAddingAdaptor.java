package com.coconason.dtf.manager.message;

import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.Set;

/**
 * @Author: Jason
 * @date: 2018/8/24-17:04
 */
public abstract class TransactionMessageForAddingAdaptor implements TransactionMessageGroupInterface<String> {

    @Override
    public void addMember(String memberId, String url, Object obj) {
    }

    @Override
    public void addMemberToGroup(TransactionMessageForAdding e) {

    }
    @Override
    public void setCtxForSubmitting(ChannelHandlerContext ctxForSubmitting) {

    }
    @Override
    public List<TransactionMessageForAdding> getMemberList() {
        return null;
    }

    @Override
    public Set<String> getMemberSet() {
        return null;
    }

    @Override
    public String getGroupId() {
        return null;
    }

    @Override
    public void setGroupId(String groupId) {

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
