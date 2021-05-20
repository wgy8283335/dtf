package com.dtf.client.core.beans.group;

import java.util.Set;

/**
 * Abstract class of base transaction group information.
 * 
 * @author wangguangyuan
 */
public abstract class BaseTransactionGroupInfo {
    
    /**
     * Current is a threadLocal variable,each thread has its own BaseTransactionGroupInfo variable.
     */
    private static final ThreadLocal<BaseTransactionGroupInfo> CURRENT = new ThreadLocal<>();
    
    /**
     * Get BaseTransactionGroupInfo object in current thread.
     * 
     * @return base transaction group information
     */
    public static BaseTransactionGroupInfo getCurrent() {
        return CURRENT.get();
    }
    
    /**
     * Set BaseTransactionGroupInfo object in current thread.
     *
     * @param transactionGroupInfo base transaction group information
     */
    public static void setCurrent(final BaseTransactionGroupInfo transactionGroupInfo) {
        CURRENT.set(transactionGroupInfo);
    }
    
    /**
     * Generate uuid as new member id, and store it in groupMembers.
     */
    public abstract void addNewMember();
    
    /**
     * Add tempSet in groupMembers.
     *
     * @param tempSet set of members
     */
    public abstract void addMembers(Set<Long> tempSet);
    
    /**
     * Get member id.
     *
     * @return member id
     */
    public abstract Long getMemberId();
    
    /**
     * Get group id.
     *
     * @return group id
     */
    public abstract String getGroupId();
    
    /**
     * Get group members.
     *
     * @return set of group members
     */
    public abstract Set<Long> getGroupMembers();

}
