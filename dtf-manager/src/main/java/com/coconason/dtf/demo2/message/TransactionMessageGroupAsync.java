package com.coconason.dtf.demo2.message;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: Jason
 * @date: 2018/9/6-11:54
 */
public class TransactionMessageGroupAsync {
    private String groupId;
    private final Set<MessageInfo> memberSet = new HashSet<MessageInfo>();

    public TransactionMessageGroupAsync(String groupId) {
        this.groupId = groupId;
    }

    public void addMember(String memberId,String url,Object obj){
        memberSet.add(new MessageInfo(memberId,false,url,obj));
    }

    public String getGroupId() {
        return groupId;
    }

    public Set<MessageInfo> getMemberSet() {
        return memberSet;
    }

    public static TransactionMessageGroupAsync parse(MessageProto.Message message){
        JSONObject map = JSONObject.parseObject(message.getInfo());
        String groupId = map.get("groupId").toString();
        TransactionMessageGroupAsync transactionMessageGroupAsync = new TransactionMessageGroupAsync(groupId);
        transactionMessageGroupAsync.addMember(map.get("groupMemberId").toString(),map.get("url").toString(),map.get("obj"));
        return transactionMessageGroupAsync;
    }

}
