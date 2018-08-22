package com.coconason.dtf.client.core.beans;

import java.lang.reflect.Method;

/**
 * @Author: Jason
 * @date: 2018/8/21-17:15
 */
public class TransactionServiceInfo {
    String groupId;
    String groupMemeberId;
    Method method;
    Object[] args;

    public TransactionServiceInfo(String groupId, String groupMemeberId, Method method, Object[] args) {
        this.groupId = groupId;
        this.groupMemeberId = groupMemeberId;
        this.method = method;
        this.args = args;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupMemeberId() {
        return groupMemeberId;
    }

    public void setGroupMemeberId(String groupMemeberId) {
        this.groupMemeberId = groupMemeberId;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
