package com.coconason.dtf.manager.message;

import java.util.Set;

/**
 * @Author: Jason
 * @date: 2018/8/24-18:43
 */
public final class TransactionMessageForSubmit extends TransactionMessageForSubmitAdaptor{
    private String groupId;
    private Set<String> memberSet;
    @Override
    public String getGroupId() {
        return groupId;
    }
    @Override
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    @Override
    public Set<String> getMemberSet() {
        return memberSet;
    }

    TransactionMessageForSubmit(String groupId, Set<String> memberSet) {
        this.groupId = groupId;
        this.memberSet = memberSet;
    }
}
