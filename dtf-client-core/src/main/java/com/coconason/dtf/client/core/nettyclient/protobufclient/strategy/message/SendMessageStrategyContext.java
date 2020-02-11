package com.coconason.dtf.client.core.nettyclient.protobufclient.strategy.message;

import com.coconason.dtf.client.core.beans.BaseTransactionServiceInfo;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;

/**
 * Send message strategy context.
 *
 * @Author: Jason
 */
public final class SendMessageStrategyContext {
    
    private Map<ActionType, SendMessageStrategy> map;
    
    private SendMessageStrategyContext() {
        map = new HashMap<>();
        SendMessageWithGroupMemberSet sendMessageWithGroupMemberSet = new SendMessageWithGroupMemberSet();
        SendMessageWithMemberId sendMessageWithMemberId = new SendMessageWithMemberId();
        SendMessageWithMethod sendMessageWithMethod = new SendMessageWithMethod();
        SendMessageWithShortParameters sendMessageWithShortParameters = new SendMessageWithShortParameters();
        SendMessageWithUrl sendMessageWithUrl = new SendMessageWithUrl();
        map.put(ActionType.ADD, sendMessageWithMethod);
        map.put(ActionType.APPLYFORSUBMIT, sendMessageWithGroupMemberSet);
        map.put(ActionType.ADD_STRONG, sendMessageWithMethod);
        map.put(ActionType.APPLYFORSUBMIT_STRONG, sendMessageWithGroupMemberSet);
        map.put(ActionType.SUB_SUCCESS_STRONG, sendMessageWithMemberId);
        map.put(ActionType.SUB_FAIL_STRONG, sendMessageWithGroupMemberSet);
        map.put(ActionType.ADD_ASYNC, sendMessageWithUrl);
        map.put(ActionType.ASYNC_COMMIT, sendMessageWithGroupMemberSet);
        map.put(ActionType.CANCEL, sendMessageWithGroupMemberSet);
        map.put(ActionType.WHOLE_SUCCESS_STRONG_ACK, sendMessageWithShortParameters);
        map.put(ActionType.WHOLE_FAIL_STRONG_ACK, sendMessageWithShortParameters);
        map.put(ActionType.SUB_SUCCESS, sendMessageWithMemberId);
        map.put(ActionType.SUB_FAIL, sendMessageWithGroupMemberSet);
        
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
