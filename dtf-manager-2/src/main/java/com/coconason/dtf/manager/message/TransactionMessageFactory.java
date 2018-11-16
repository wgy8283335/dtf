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
 * @Author: Jason
 * @date: 2018/10/4-17:30
 */
public class TransactionMessageFactory {

    public static TransactionMessageGroupInterface getMessageForSubmitInstance(MessageProto.Message message) {
        JSONObject info = JSONObject.parseObject(message.getInfo());
        String groupId = info.get("groupId").toString();
        String[] strArray = info.get("groupMemberSet").toString().replace("[","").replace("]","").split(", ");
        Set<String> memberSet =  new HashSet<String>();
        CollectionUtils.addAll(memberSet,strArray);
        TransactionMessageGroupInterface transactionMessageForSubmit = new TransactionMessageForSubmit(groupId,memberSet);
        return transactionMessageForSubmit;
    }

    public static TransactionMessageGroupInterface getMessageGroupInstance(MessageProto.Message message, ChannelHandlerContext ctx){
        JSONObject info = JSONObject.parseObject(message.getInfo());
        String groupId = info.get("groupId").toString();
        String groupMemberId = info.get("groupMemberId").toString();
        String method = info.get("method").toString();
        String args = info.get("args").toString();
        TransactionMessageForAddingInterface tmfa = new TransactionMessageForAdding(groupMemberId,ctx,method,args);
        List<TransactionMessageForAddingInterface> memberList = new ArrayList<TransactionMessageForAddingInterface>();
        Set<String> memberSet = new HashSet<String>();
        memberList.add(tmfa);
        memberSet.add(tmfa.getGroupMemberId());
        TransactionMessageGroupInterface result = new TransactionMessageGroup(groupId);
        result.addMemberToGroup(tmfa);
        return result;
    }

    public static TransactionMessageGroupInterface getMessageGroupAsyncInstance(MessageProto.Message message){
        JSONObject map = JSONObject.parseObject(message.getInfo());
        String groupId = map.get("groupId").toString();
        TransactionMessageGroupInterface transactionMessageGroupAsync = new TransactionMessageGroupAsync(groupId);
        transactionMessageGroupAsync.addMember(map.get("groupMemberId").toString(),map.get("url").toString(),map.get("obj"));
        return transactionMessageGroupAsync;
    }
}
