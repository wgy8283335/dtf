package com.dtf.client.core.nettyclient.protobufclient.strategy.message;

import com.alibaba.fastjson.JSONObject;
import com.dtf.client.core.beans.service.BaseTransactionServiceInfo;
import com.dtf.common.protobuf.MessageProto;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Implementation class of SendMessageStrategy interface.
 *
 * @Author: wangguangyuan
 */
public class SendMessageWithMethod implements SendMessageStrategy {

    /**
     * Logger of SendMessageStrategy class.
     */
    private Logger logger = LoggerFactory.getLogger(SendMessageStrategy.class);

    /**
     * Send message according to action.
     *
     * @param ctx channel handler context
     * @param serviceInfo base transction service info
     */
    @Override
    public void sendMsg(final ChannelHandlerContext ctx, final BaseTransactionServiceInfo serviceInfo) {
        JSONObject info = new JSONObject();
        info.put("groupId", serviceInfo.getInfo().get("groupId").toString());
        info.put("groupMemberId", serviceInfo.getInfo().get("groupMemberId").toString());
        info.put("method", (Method) serviceInfo.getInfo().get("method"));
        info.put("args", (Object[]) serviceInfo.getInfo().get("args"));
        MessageProto.Message.Builder builder = MessageProto.Message.newBuilder();
        builder.setInfo(info.toJSONString());
        builder.setId(serviceInfo.getId());
        builder.setAction(serviceInfo.getAction());
        MessageProto.Message message = builder.build();
        logger.debug("Send transaction message:\n" + message);
        ctx.writeAndFlush(message);
    }
}
