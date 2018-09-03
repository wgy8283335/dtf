package com.coconason.dtf.demo2.message;

import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Method;

/**
 * @Author: Jason
 * @date: 2018/8/24-17:01
 */
public class TransactionMessageForAdding {
    private String groupMemberId;
    private ChannelHandlerContext ctx;
    private String method;
    private String args;

    public TransactionMessageForAdding(String groupMemberId, ChannelHandlerContext ctx, String method, String args) {
        this.groupMemberId = groupMemberId;
        this.ctx = ctx;
        this.method = method;
        this.args = args;
    }

    public String getGroupMemberId() {
        return groupMemberId;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public String getMethod() {
        return method;
    }

    public String getArgs() {
        return args;
    }
}
