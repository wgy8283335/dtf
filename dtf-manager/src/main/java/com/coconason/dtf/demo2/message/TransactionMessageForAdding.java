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
    private Method method;
    private Object[] args;

    public TransactionMessageForAdding(String groupMemberId, ChannelHandlerContext ctx, Method method, Object[] args) {
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

    public Method getMethod() {
        return method;
    }

    public Object[] getArgs() {
        return args;
    }
}
