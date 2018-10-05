package com.coconason.dtf.client.core.beans;

import com.coconason.dtf.common.utils.UuidGenerator;

import java.util.Set;

/**
 * @Author: Jason
 * @date: 2018/8/21-13:31
 */
public final class TransactionGroupInfo extends BaseTransactionGroupInfo {

    private String groupId;
    private Long memberId;
    private Set<Long> groupMembers;

    @Override
    public void addNewMemeber(){
        this.memberId = UuidGenerator.generateLongId();
        groupMembers.add(this.memberId);
    }
    @Override
    public void addMemebers(Set<Long> tempSet){
        groupMembers.addAll(tempSet);
    }
    @Override
    public Long getMemberId() {
        return memberId;
    }
    @Override
    public String getGroupId() {
        return groupId;
    }
    @Override
    public Set<Long> getGroupMembers() {
        return groupMembers;
    }
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(groupId);
        buffer.append("-");
        buffer.append(memberId);
        for(Long member:groupMembers){
            buffer.append("-");
            buffer.append(member);
        }
        return buffer.toString();
    }

    TransactionGroupInfo(String groupId,Long memberId, Set<Long> groupMembers) {
        this.groupId = groupId;
        this.memberId = memberId;
        this.groupMembers = groupMembers;
    }
}
