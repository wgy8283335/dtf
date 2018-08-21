package com.coconason.dtf.client.core.beans;

/**
 * @Author: Jason
 * @date: 2018/8/21-17:15
 */
public class TransactionServiceInfo {
    String groupId;
    String groupMemeberId;
    String serviceLink;
    String type;
    String params;

    public TransactionServiceInfo(String groupId, String groupMemeberId, String serviceLink, String type, String params) {
        this.groupId = groupId;
        this.groupMemeberId = groupMemeberId;
        this.serviceLink = serviceLink;
        this.type = type;
        this.params = params;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupMemeberId() {
        return groupMemeberId;
    }

    public String getServiceLink() {
        return serviceLink;
    }

    public String getType() {
        return type;
    }

    public String getParams() {
        return params;
    }
}
