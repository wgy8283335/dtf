package com.dtf.manager.message;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Transaction message group.
 *
 * @Author: wangguangyuan
 */
public final class TransactionMessageGroup extends TransactionMessageGroupAdapter {
    
    /**
     * Group id.
     */
    private String groupId;
    
    /**
     * List of transaction message for adding.
     */
    private List<TransactionMessageForAddingInterface> memberList;
    
    /**
     * Group member set.
     */
    private Set<String> memberSet;
    
    /**
     * Channel handler context.
     */
    private ChannelHandlerContext ctxForSubmitting;
    
    public TransactionMessageGroup(final String groupId) {
        this.groupId = groupId;
        this.memberList = new ArrayList<TransactionMessageForAddingInterface>();
        this.memberSet = new HashSet<String>();
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
     * Get member list.
     *
     * @return list of TransactionMessageForAddingInterface
     */
    @Override
    public List<TransactionMessageForAddingInterface> getMemberList() {
        return memberList;
    }
    
    /**
     * Get member set.
     *
     * @return set of member
     */
    @Override
    public Set<String> getMemberSet() {
        return memberSet;
    }
    
    /**
     * Add member to group.
     *
     * @param e transaction message for adding
     */
    @Override
    public void addMemberToGroup(final TransactionMessageForAddingInterface e) {
        memberList.add(e);
        memberSet.add(e.getGroupMemberId());
    }
    
    /**
     * Set channel handler context for submitting.
     *
     * @param ctxForSubmitting channel handler context
     */
    @Override
    public void setCtxForSubmitting(final ChannelHandlerContext ctxForSubmitting) {
        this.ctxForSubmitting = ctxForSubmitting;
    }
    
    /**
     * Get channel handler context.
     *
     * @return channel handler context
     */
    @Override
    public ChannelHandlerContext getCtx() {
        return ctxForSubmitting;
    }
    
    /**
     * Override toString method.
     *
     * @return string
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{ groupId=" + groupId);
        for (TransactionMessageForAddingInterface memberMsg : memberList) {
            stringBuilder.append(memberMsg.toString());
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
    
}
