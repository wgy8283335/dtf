package com.coconason.dtf.client.core.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Jason
 * @date: 2018/8/21-13:31
 */
public class TransactionGroupInfo {

    private final static ThreadLocal<TransactionGroupInfo> current = new ThreadLocal<>();

    private String groupId;
    private List<Integer> groupMembers;

    public TransactionGroupInfo(String groupId) {
        this.groupId = groupId;
        this.groupMembers = new ArrayList<>();
    }

    public void addMemeber(Integer memberId){
        groupMembers.add(memberId);
    }

    public String getGroupId() {
        return groupId;
    }

    public List<Integer> getGroupMembers() {
        return groupMembers;
    }

    public static TransactionGroupInfo getCurrent(){
        return current.get();
    }

    public static void setCurrent(TransactionGroupInfo transactionGroupInfo){
        current.set(transactionGroupInfo);
    }
}
