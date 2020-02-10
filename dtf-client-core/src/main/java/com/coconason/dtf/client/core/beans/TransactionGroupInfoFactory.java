package com.coconason.dtf.client.core.beans;

import java.util.HashSet;
import java.util.Set;

/**
 * Transaction group information factory
 * 
 * @Author: Jason
 */
public class TransactionGroupInfoFactory {

    /**
     * Get instance by groupId and memberId.
     * 
     * @param groupId
     * @param memberId
     * @return base transction group information
     */
    public static BaseTransactionGroupInfo getInstance(String groupId, Long memberId){
        Set groupMembers = new HashSet<>();
        groupMembers.add(memberId);
        return new TransactionGroupInfo(groupId, memberId, groupMembers);
    }

    /**
     * Get instance by parsing string
     * 
     * @param value
     * @return base transaction group information
     */
    public static BaseTransactionGroupInfo getInstanceByParsingString(String value){
        String[] array = value.split("-");
        String groupId = array[0];
        Long memberId = Long.valueOf(array[1]);
        Set<Long> groupMembers = new HashSet<>();
        for(int i=2; i<array.length;i++){
            groupMembers.add(Long.valueOf(array[i]));
        }
        BaseTransactionGroupInfo transactionGroupInfo = new TransactionGroupInfo(groupId,memberId,groupMembers);
        return transactionGroupInfo;
    }
}
