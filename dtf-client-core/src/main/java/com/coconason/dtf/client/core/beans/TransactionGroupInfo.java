package com.coconason.dtf.client.core.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Jason
 * @date: 2018/8/21-13:31
 */
public class TransactionGroupInfo {
    private String groupId;
    private List<Integer> groupMembers;
    private String groupMemberId;

    public TransactionGroupInfo(String groupId) {
        this.groupId = groupId;
        this.groupMembers = new ArrayList<>();
    }

    public void addMemeber(Integer memberId){
        groupMemberId = memberId.toString();
        groupMembers.add(memberId);
    }

    public String getGroupId() {
        return groupId;
    }

    public List<Integer> getGroupMembers() {
        return groupMembers;
    }

    public String getGroupMemberId() {
        return groupMemberId;
    }
}
