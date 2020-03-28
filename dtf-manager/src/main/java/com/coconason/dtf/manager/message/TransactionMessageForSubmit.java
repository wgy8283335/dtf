package com.coconason.dtf.manager.message;

import java.util.Set;

/**
 * Transaction message for submitting.
 * 
 * @Author: Jason
 */
public final class TransactionMessageForSubmit extends TransactionMessageForSubmitAdapter {
    
    /**
     * Group id.
     */
    private String groupId;
    
    /**
     * Group member set.
     */
    private Set<String> memberSet;
        
    TransactionMessageForSubmit(final String groupId, final Set<String> memberSet) {
        this.groupId = groupId;
        this.memberSet = memberSet;
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
     * Set group id.
     * 
     * @param groupId group id
     */
    @Override
    public void setGroupId(final String groupId) {
        this.groupId = groupId;
    }
    
    /**
     * Get member set.
     * 
     * @return member set
     */
    @Override
    public Set<String> getMemberSet() {
        return memberSet;
    }
    
}
