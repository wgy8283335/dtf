package com.coconason.dtf.client.core.nettyclient.protobufclient.strategy.message;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.client.core.beans.service.TransactionServiceInfo;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SendMessageStrategyContextTest {
    
    @Test
    public void testProcessSignalAccordingToAction() {
        testByActionType("974950438", ActionType.ADD);
        testByActionType("974950438", ActionType.APPLYFORSUBMIT);
        testByActionType("974950438", ActionType.ADD_STRONG);
        testByActionType("974950438", ActionType.APPLYFORSUBMIT_STRONG);
        testByActionType("974950438", ActionType.SUB_SUCCESS_STRONG);
        testByActionType("974950438", ActionType.SUB_FAIL_STRONG);
        testByActionType("974950438", ActionType.ADD_ASYNC);
        testByActionType("974950438", ActionType.ASYNC_COMMIT);
        testByActionType("974950438", ActionType.CANCEL);
        testByActionType("974950438", ActionType.WHOLE_SUCCESS_STRONG_ACK);
        testByActionType("974950438", ActionType.WHOLE_FAIL_STRONG_ACK);
        testByActionType("974950438", ActionType.SUB_SUCCESS);
        testByActionType("974950438", ActionType.SUB_FAIL);
    }
    
    private void testByActionType(String id, ActionType type) {
        ChannelHandlerContext ctx = createChannelHandlerContext();
        JSONObject jsonObject = createJSONObject();
        SendMessageStrategyContext.getInstance().processSignalAccordingToAction(ctx, new TransactionServiceInfo(id, jsonObject, type));
    }
    
    private ChannelHandlerContext createChannelHandlerContext() {
        ChannelHandlerContext result = mock(ChannelHandlerContext.class);
        when(result.writeAndFlush(any())).thenReturn(null);
        return result;
    }
    
    private JSONObject createJSONObject() {
        Set set = new HashSet<Long>();
        set.add(23L);
        JSONObject result = new JSONObject();
        result.put("groupId", "1231");
        result.put("groupMemberId", 23L);
        result.put("method", null);
        result.put("args", new Object[3]);
        result.put("groupMemberSet", set);
        result.put("memberId", 45L);
        result.put("url", "http://test-url");
        result.put("obj", null);
        result.put("httpAction", "post");
        return result;
    }
}
