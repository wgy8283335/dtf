package com.dtf.client.core.nettyclient.protobufclient.strategy.message;

import com.dtf.client.core.beans.service.BaseTransactionServiceInfo;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

import com.dtf.common.protobuf.MessageProto.Message.ActionType;

/**
 * Send message strategy context.
 *
 * @author wangguangyuan
 */
public final class SendMessageStrategyContext {
    
    private Map<ActionType, SendMessageStrategy> map;
    
    private SendMessageStrategyContext() {
        map = new HashMap<>();
        map.put(ActionType.ADD, new SendMessageWithMethod());
        map.put(ActionType.APPLYFORSUBMIT, new SendMessageWithGroupMemberSet());
        map.put(ActionType.ADD_STRONG, new SendMessageWithMethod());
        map.put(ActionType.APPLYFORSUBMIT_STRONG, new SendMessageWithGroupMemberSet());
        map.put(ActionType.SUB_SUCCESS_STRONG, new SendMessageWithMemberId());
        map.put(ActionType.SUB_FAIL_STRONG, new SendMessageWithMemberId());
        map.put(ActionType.ADD_ASYNC, new SendMessageWithUrl());
        map.put(ActionType.ASYNC_COMMIT, new SendMessageWithGroupMemberSet());
        map.put(ActionType.CANCEL, new SendMessageWithGroupMemberSet());
        map.put(ActionType.WHOLE_SUCCESS_STRONG_ACK, new SendMessageWithShortParameters());
        map.put(ActionType.WHOLE_FAIL_STRONG_ACK, new SendMessageWithShortParameters());
        map.put(ActionType.SUB_SUCCESS, new SendMessageWithMemberId());
        map.put(ActionType.SUB_FAIL, new SendMessageWithMemberId());
        
    }
    
    /**
     * Get single instance.
     * 
     * @return send message strategy context instance
     */
    public static SendMessageStrategyContext getInstance() {
        return SingleHolder.INSTANCE;
    }

    /**
     * Send message according to action.
     * 
     * @param ctx channel handler context
     * @param serviceInfo base transaction service information
     */
    public void processSignalAccordingToAction(final ChannelHandlerContext ctx, final BaseTransactionServiceInfo serviceInfo) {
        SendMessageStrategy sendMessageStrategy = this.map.get(serviceInfo.getAction());
        sendMessageStrategy.sendMsg(ctx, serviceInfo);
    }
    
    private static class SingleHolder {
        private static final SendMessageStrategyContext INSTANCE = new SendMessageStrategyContext();
    }
    
}
