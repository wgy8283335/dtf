package com.coconason.dtf.server.message;

import java.lang.reflect.Method;

/**
 * @Author: Jason
 * @date: 2018/8/24-17:01
 */
public class TransactionMessageForAdding {
    private String groupMemberId;
    private Method method;
    private Object[] args;

    public TransactionMessageForAdding(String groupMemberId, Method method, Object[] args) {
        this.groupMemberId = groupMemberId;
        this.method = method;
        this.args = args;
    }

    public String getGroupMemeberId() {
        return groupMemberId;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getArgs() {
        return args;
    }
}
