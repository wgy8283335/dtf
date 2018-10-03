package com.coconason.dtf.manager.message;

import io.netty.channel.ChannelHandlerContext;

/**
 * @Author: Jason
 * @date: 2018/8/24-17:01
 */
public class TransactionMessageForAdding implements TransactionMessageForAddingInterface {
    private String groupMemberId;
    private ChannelHandlerContext ctx;
    private String method;
    private String args;
    private boolean commited = false;

    public TransactionMessageForAdding(String groupMemberId, ChannelHandlerContext ctx, String method, String args) {
        this.groupMemberId = groupMemberId;
        this.ctx = ctx;
        this.method = method;
        this.args = args;
    }
    @Override
    public String getGroupMemberId() {
        return groupMemberId;
    }
    @Override
    public ChannelHandlerContext getCtx() {
        return ctx;
    }
    @Override
    public Boolean isCommitted() {
        return commited;
    }
    @Override
    public void setCommitted(boolean commited) {
        this.commited = commited;
    }

    @Override
    public String toString() {
        return "groupMemberId='" + groupMemberId + '\'' +
                ", ctx=" + ctx +
                ", method='" + method + '\'' +
                ", args='" + args + '\'' +
                ", commited=" + commited;
    }
}
