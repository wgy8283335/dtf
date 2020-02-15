package com.coconason.dtf.manager.message;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Transaction message group for asynchronous.
 * 
 * @Author: Jason
 */
public final class TransactionMessageGroupAsync extends TransactionMessageGroupAsyncAdaptor {
    
    /**
     * Group id.
     */
    private String groupId;

    /**
     * Set of message information interface.
     */
    private Set<MessageInfoInterface> memberSet;

    TransactionMessageGroupAsync(final String groupId) {
        this.groupId = groupId;
        this.memberSet = new HashSet<MessageInfoInterface>();
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
     * Get member set.
     *
     * @return set of member
     */
    @Override
    public Set<MessageInfoInterface> getMemberSet() {
        return memberSet;
    }
    
    /**
     * Get member list.
     *
     * @return list of TransactionMessageForAddingInterface
     */
    @Override
    public List<TransactionMessageForAddingInterface> getMemberList() {
        return null;
    }
    
    /**
     * Add member to member set.
     *
     * @param memberId member id
     * @param url url of request
     * @param obj parameters of request
     */
    @Override
    public void addMember(final String memberId, final String url, final Object obj) {
        memberSet.add(new MessageInfo(memberId, false, url, obj));
    }
    
    /**
     * Over ride equals method.
     *
     * @param o MessageInfo object
     * @return true or false
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        TransactionMessageGroupAsync temp = (TransactionMessageGroupAsync) o;
        if (temp.getGroupId() == null || this.getGroupId() == null || (!this.groupId.equals(temp.getGroupId()))) {
            return false;
        }
        if (memberSet != null) {
            memberSet.removeAll(temp.getMemberSet());
            if (!memberSet.isEmpty()) {
                return false;
            }
        } else if (memberSet == null || temp.getMemberSet() == null) {
            return false;
        }
        return true;
    }
    
    /**
     * Over ride hashCode method.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        int result = getGroupId() != null ? getGroupId().hashCode() : 0;
        result = 31 * result + (getMemberSet() != null ? getMemberSet().hashCode() : 0);
        return result;
    }
    
}
