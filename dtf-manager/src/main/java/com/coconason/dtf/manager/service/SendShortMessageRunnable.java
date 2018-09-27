package com.coconason.dtf.manager.service;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.common.utils.UuidGenerator;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Author: Jason
 * @date: 2018/9/27-14:29
 */
public class SendShortMessageRunnable implements Runnable {
    private String groupId;
    private MessageProto.Message.ActionType actionType;
    private ChannelHandlerContext ctx;

    public SendShortMessageRunnable(String groupId, MessageProto.Message.ActionType actionType, ChannelHandlerContext ctx) {
        this.groupId = groupId;
        this.actionType = actionType;
        this.ctx = ctx;
    }

    @Override
    public void run() {
        MessageProto.Message.Builder builder= MessageProto.Message.newBuilder();
        JSONObject info = new JSONObject();
        info.put("groupId",groupId);
        builder.setInfo(info.toJSONString());
        builder.setId(UuidGenerator.generateUuid());
        builder.setAction(actionType);
        MessageProto.Message message = builder.build();
        System.out.println("Send transaction message:\n" + message);
        ctx.writeAndFlush(message);
    }
}
