package com.coconason.dtf.manager.utils;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import com.coconason.dtf.common.utils.UuidGenerator;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: Jason
 * @date: 2018/9/19-10:32
 */
public final class MessageSender {

    private Logger logger = LoggerFactory.getLogger(MessageSender.class);

    public static void sendMsg(String groupId,ActionType action,ChannelHandlerContext ctx){
        MessageProto.Message.Builder builder= MessageProto.Message.newBuilder();
        JSONObject info = new JSONObject();
        info.put("groupId",groupId);
        builder.setInfo(info.toJSONString());
        builder.setId(UuidGenerator.generateUuid());
        builder.setAction(action);
        MessageProto.Message message = builder.build();
        System.out.println("Send transaction message:\n" + message);
        ctx.writeAndFlush(message);
    }
}
