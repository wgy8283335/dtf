package com.coconason.dtf.demo2.message;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author: Jason
 * @date: 2018/8/24-17:04
 */
public class TransactionMessageGroup {
    private String groupId;
    private final List<TransactionMessageForAdding> memberList = new ArrayList<TransactionMessageForAdding>();
    private final Set<String> memberSet = new HashSet<String>();


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<TransactionMessageForAdding> getMemberList() {
        return memberList;
    }

    public Set<String> getMemberSet() {
        return memberSet;
    }

    public TransactionMessageGroup(MessageProto.Message message, ChannelHandlerContext ctx){
        JSONObject info = JSONObject.parseObject(message.getInfo());
        String groupId = info.get("groupId").toString();
        this.groupId = groupId;
        String groupMemeberId = info.get("groupMemeberId").toString();
        String method = info.get("method").toString();
        String args = info.get("args").toString();
        TransactionMessageForAdding tmfa = new TransactionMessageForAdding(groupMemeberId,ctx,method,args);
        addMemberToGroup(tmfa);
    }

    public void addMemberToGroup(TransactionMessageForAdding e){
        memberList.add(e);
        memberSet.add(e.getGroupMemberId());
    }


}
