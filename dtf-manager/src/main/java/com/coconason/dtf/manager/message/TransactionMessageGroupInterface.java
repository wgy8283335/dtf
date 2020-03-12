package com.coconason.dtf.manager.message;

import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.Set;

/**
 * Interface of transaction message group.
 * 
 * @Author: Jason
 */
public interface TransactionMessageGroupInterface<T> {
    
    /**
     * Get group id.
     * 
     * @return group id
     */
    String getGroupId();
    
    /**
     * Set group id.
     * 
     * @param groupId group id
     */
    void setGroupId(String groupId);
    
    /**
     * Get member list.
     * 
     * @return list of TransactionMessageForAddingInterface
     */
    List<TransactionMessageForAddingInterface> getMemberList();
    
    /**
     * Get member set.
     * 
     * @return set of member
     */
    Set<T> getMemberSet();
    
    /**
     * Add member to member set.
     *
     * @param memberId member id
     * @param url url of request
     * @param obj parameters of request
     */
    void addMember(String memberId, String url, Object obj, String httpAction);
    
    /**
     * Add member to group.
     * 
     * @param e transaction message for adding
     */
    void addMemberToGroup(TransactionMessageForAddingInterface e);
    
    /**
     * Set channel handler context for submitting.
     * 
     * @param ctxForSubmitting channel handler context
     */
    void setCtxForSubmitting(ChannelHandlerContext ctxForSubmitting);
    
    /**
     * Get channel handler context.
     * 
     * @return channel handler context
     */
    ChannelHandlerContext getCtx();
    
    /**
     * Get group member id.
     * 
     * @return group member id
     */
    String getGroupMemberId();
    
    /**
     * Set committed value.
     * 
     * @param commited true or false
     */
    void setCommitted(boolean commited);
    
    /**
     * Check value of  isCommitted.
     * 
     * @return value of isCommitted
     */
    Boolean isCommitted();
    
    /**
     * Get url of request.
     * 
     * @return url
     */
    String getUrl();
    
    /**
     * Get parameters of request.
     * 
     * @return parameters
     */
    Object getObj();
    
}
