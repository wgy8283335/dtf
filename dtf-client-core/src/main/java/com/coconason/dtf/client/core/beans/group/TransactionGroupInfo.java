package com.coconason.dtf.client.core.beans.group;

import com.coconason.dtf.common.utils.UuidGenerator;

import java.util.Set;

/**
 * Transaction group information.
 * 
 * @Author: Jason
 */
public final class TransactionGroupInfo extends BaseTransactionGroupInfo {
    
    private String groupId;
    
    private Long memberId;
    
    private Set<Long> groupMembers;

    TransactionGroupInfo(final String groupId, final Long memberId, final Set<Long> groupMembers) {
        this.groupId = groupId;
        this.memberId = memberId;
        this.groupMembers = groupMembers;
    }
    
    /**
     * Generate uuid as new member id, and store it in groupMembers.
     */
    @Override
    public void addNewMember() {
        this.memberId = UuidGenerator.generateLongId();
        groupMembers.add(this.memberId);
    }
    
    /**
     * Add tempSet in groupMembers.
     * 
     * @param tempSet set of members
     */
    @Override
    public void addMembers(final Set<Long> tempSet) {
        groupMembers.addAll(tempSet);
    }
    
    /**
     * Get member id.
     *
     * @return member id
     */
    @Override
    public Long getMemberId() {
        return memberId;
    }
    
    /**
     * Get group id.
     * 
     * @return group id
     */
    @Override
    public String getGroupId() {
        return groupId;
    }
    
    /**
     * Get group members.
     * 
     * @return set of group members
     */
    @Override
    public Set<Long> getGroupMembers() {
        return groupMembers;
    }

    /**
     * Generate string for group id, member id and group members. 
     * 
     * @return group id, member id and group members in String
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(groupId);
        buffer.append("-");
        buffer.append(memberId);
        for (Long member:groupMembers) {
            buffer.append("-");
            buffer.append(member);
        }
        return buffer.toString();
    }
    
}
