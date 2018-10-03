package com.coconason.dtf.manager.message;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author: Jason
 * @date: 2018/8/24-17:04
 */
public class TransactionMessageGroup extends TransactionMessageGroupAdaptor {
    private String groupId;
    private final List<TransactionMessageForAddingInterface> memberList = new ArrayList<TransactionMessageForAddingInterface>();
    private final Set<String> memberSet = new HashSet<String>();
    private ChannelHandlerContext ctxForSubmitting;

    public TransactionMessageGroup(MessageProto.Message message, ChannelHandlerContext ctx){
        JSONObject info = JSONObject.parseObject(message.getInfo());
        String groupId = info.get("groupId").toString();
        this.groupId = groupId;
        String groupMemberId = info.get("groupMemberId").toString();
        String method = info.get("method").toString();
        String args = info.get("args").toString();
        TransactionMessageForAddingInterface tmfa = new TransactionMessageForAdding(groupMemberId,ctx,method,args);
        addMemberToGroup(tmfa);
    }
    @Override
    public String getGroupId(){
        return groupId;
    }
    @Override
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    @Override
    public List<TransactionMessageForAddingInterface> getMemberList() {
        return memberList;
    }
    @Override
    public Set<String> getMemberSet() {
        return memberSet;
    }

    @Override
    public void addMemberToGroup(TransactionMessageForAddingInterface e){
        memberList.add(e);
        memberSet.add(e.getGroupMemberId());
    }
    @Override
    public void setCtxForSubmitting(ChannelHandlerContext ctxForSubmitting) {
        this.ctxForSubmitting = ctxForSubmitting;
    }
    @Override
    public ChannelHandlerContext getCtx() {
        return ctxForSubmitting;
    }
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{ groupId="+groupId);
        for(TransactionMessageForAddingInterface memberMsg : memberList){
            stringBuilder.append(memberMsg.toString());
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
