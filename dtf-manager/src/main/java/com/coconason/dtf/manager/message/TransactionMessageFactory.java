package com.coconason.dtf.manager.message;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Factory of creating transaction message.
 * 
 * @Author: Jason
 */
public class TransactionMessageFactory {
    
    /**
     * Creating transaction message for submitting.
     * 
     * @param message message in proto
     * @return transaction message for submit
     */
    public static TransactionMessageGroupInterface getMessageForSubmitInstance(final MessageProto.Message message) {
        JSONObject info = JSONObject.parseObject(message.getInfo());
        String groupId = info.get("groupId").toString();
        String[] strArray = info.get("groupMemberSet").toString().replace("[", "").replace("]", "").split(",");
        Set<String> memberSet = new HashSet<String>();
        CollectionUtils.addAll(memberSet, strArray);
        TransactionMessageGroupInterface transactionMessageForSubmit = new TransactionMessageForSubmit(groupId, memberSet);
        return transactionMessageForSubmit;
    }
    
    /**
     * Creating transaction message group.
     * 
     * @param message message in proto
     * @param ctx channel handler context
     * @return message group instance
     */
    public static TransactionMessageGroupInterface getMessageGroupInstance(final MessageProto.Message message, final ChannelHandlerContext ctx) {
        JSONObject info = JSONObject.parseObject(message.getInfo());
        String groupId = info.get("groupId").toString();
        String groupMemberId = info.get("groupMemberId").toString();
        String method = info.get("method").toString();
        String args = info.get("args").toString();
        TransactionMessageForAddingInterface tmfa = new TransactionMessageForAdding(groupMemberId, ctx, method, args);
        TransactionMessageGroupInterface result = new TransactionMessageGroup(groupId);
        result.addMemberToGroup(tmfa);
        return result;
    }
    
    /**
     * Creating transaction message asynchronous group.
     * 
     * @param message message in proto
     * @return transaction message group
     */
    public static TransactionMessageGroupInterface getMessageGroupAsyncInstance(final MessageProto.Message message) {
        JSONObject map = JSONObject.parseObject(message.getInfo());
        String groupId = map.get("groupId").toString(); 
        TransactionMessageGroupInterface transactionMessageGroupAsync = new TransactionMessageGroupAsync(groupId);
        transactionMessageGroupAsync.addMember(map.get("groupMemberId").toString(), map.get("url").toString(), map.get("obj"), map.get("httpAction").toString());
        return transactionMessageGroupAsync;
    }
    
}
