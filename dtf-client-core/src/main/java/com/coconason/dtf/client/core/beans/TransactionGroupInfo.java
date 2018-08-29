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
    private Long memberId;
    private Set<Long> groupMembers;

    public TransactionGroupInfo(String groupId,Long memberId) {
        this.groupId = groupId;
        this.groupMembers = new HashSet<>();
        this.memberId = memberId;
        this.groupMembers.add(memberId);
    }

    public TransactionGroupInfo(String groupId,Long memberId, Set<Long> groupMembers) {
        this.groupId = groupId;
        this.memberId = memberId;
        this.groupMembers = groupMembers;
    }

    public TransactionGroupInfo(String groupId, Set<Long> groupMembers) {
        this.groupId = groupId;
        this.groupMembers = groupMembers;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setGroupMembers(Set<Long> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public void addNewMemeber(){
        this.memberId++;
        groupMembers.add(this.memberId);
    }

    public void addMemeber(Long memberId){
        groupMembers.add(memberId);
    }

    public void addMemebers(Set<Long> tempSet){
        groupMembers.addAll(tempSet);
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getGroupId() {
        return groupId;
    }

    public Set<Long> getGroupMembers() {
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
        for(Long member:groupMembers){
            buffer.append("-");
            buffer.append(member);
        }
        return buffer.toString();
    }

    public static TransactionGroupInfo parse(String value){
        String[] array = value.split("-");
        String groupId = array[0];
        Long memberId = Long.valueOf(array[1]);
        Set<Long> groupMembers = new HashSet<>();
        for(int i=2; i<array.length;i++){
            groupMembers.add(Long.valueOf(array[i]));
        }
        TransactionGroupInfo transactionGroupInfo = new TransactionGroupInfo(groupId,memberId,groupMembers);
        return null;
    }
}
