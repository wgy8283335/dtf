package com.coconason.dtf.client.core.beans;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author: Jason
 * @date: 2018/8/21-13:31
 */
public class TransactionGroupInfo {

    private final static ThreadLocal<TransactionGroupInfo> current = new ThreadLocal<>();

    private String groupId;
    private Set<Integer> groupMembers;

    public TransactionGroupInfo(String groupId) {
        this.groupId = groupId;
        this.groupMembers = new HashSet<>();
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setGroupMembers(Set<Integer> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public void addMemeber(Integer memberId){
        groupMembers.add(memberId);
    }

    public String getGroupId() {
        return groupId;
    }

    public Set<Integer> getGroupMembers() {
        return groupMembers;
    }

    public static TransactionGroupInfo getCurrent(){
        return current.get();
    }

    public static void setCurrent(TransactionGroupInfo transactionGroupInfo){
        current.set(transactionGroupInfo);
    }
}
