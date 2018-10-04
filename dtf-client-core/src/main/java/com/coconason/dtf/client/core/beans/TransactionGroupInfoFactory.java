package com.coconason.dtf.client.core.beans;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: Jason
 * @date: 2018/10/4-11:04
 */
public class TransactionGroupInfoFactory {

    public static BaseTransactionGroupInfo getInstanceWithGroupidMemid(String groupId, Long memberId){
        Set groupMembers = new HashSet<>();
        groupMembers.add(memberId);
        return new TransactionGroupInfo(groupId, memberId, groupMembers);
    }

    public static BaseTransactionGroupInfo getInstanceParseString(String value){
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
