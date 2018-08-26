package com.coconason.dtf.client.core.beans;

import java.util.Set;
import java.util.HashSet;

/**
 * @Author: Jason
 * @date: 2018/8/21-13:31
 */
public class TransactionGroupInfo {

    private final static ThreadLocal<TransactionGroupInfo> current = new ThreadLocal<>();

    private String groupId;
    private Integer memberId;
    private Set<Integer> groupMembers;

    public TransactionGroupInfo(String groupId,Integer memberId) {
        this.groupId = groupId;
        this.groupMembers = new HashSet<>();
        this.memberId = memberId;
        this.groupMembers.add(memberId);
    }

    public TransactionGroupInfo(String groupId,Integer memberId, Set<Integer> groupMembers) {
        this.groupId = groupId;
        this.memberId = memberId;
        this.groupMembers = groupMembers;
    }

    public TransactionGroupInfo(String groupId, Set<Integer> groupMembers) {
        this.groupId = groupId;
        this.groupMembers = groupMembers;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setGroupMembers(Set<Integer> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public void addNewMemeber(Integer memberId){
        this.memberId = memberId+1;
        groupMembers.add(this.memberId);
    }

    public void addMemeber(Integer memberId){
        groupMembers.add(memberId);
    }

    public void addMemebers(Set<Integer> tempSet){
        groupMembers.addAll(tempSet);
    }

    public Integer getMemberId() {
        return memberId;
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

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(groupId);
        buffer.append(memberId);
        for(Integer member:groupMembers){
            buffer.append("-");
            buffer.append(member);
        }
        return buffer.toString();
    }

    public static TransactionGroupInfo parse(String value){
        String[] array = value.split("-");
        String groupId = array[0];
        Integer memberId = Integer.valueOf(array[1]);
        Set<Integer> groupMembers = new HashSet<>();
        for(int i=2; i<array.length;i++){
            groupMembers.add(Integer.valueOf(array[i]));
        }
        TransactionGroupInfo transactionGroupInfo = new TransactionGroupInfo(groupId,memberId,groupMembers);
        return null;
    }
}
