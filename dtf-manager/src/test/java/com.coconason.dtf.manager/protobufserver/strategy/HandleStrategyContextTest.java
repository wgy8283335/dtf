package com.coconason.dtf.manager.protobufserver.strategy;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import com.coconason.dtf.manager.cache.MessageAsyncCache;
import com.coconason.dtf.manager.cache.MessageAsyncQueueProxy;
import com.coconason.dtf.manager.cache.MessageCacheInterface;
import com.coconason.dtf.manager.cache.MessageForSubmitAsyncCache;
import com.coconason.dtf.manager.cache.MessageForSubmitSyncCache;
import com.coconason.dtf.manager.cache.MessageSyncCache;
import com.coconason.dtf.manager.message.TransactionMessageGroupInterface;
import com.coconason.dtf.manager.protobufserver.ServerTransactionHandler;
import com.coconason.dtf.manager.protobufserver.strategy.HandleStrategyContext;
import com.coconason.dtf.manager.thread.LockAndConditionInterface;
import com.coconason.dtf.manager.thread.ServerThreadLockCacheProxy;
import com.coconason.dtf.manager.threadpools.ThreadPoolForServerProxy;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HandleStrategyContextTest {
    
    @Test
    public void testHandleMessageAccordingToAction() {
        HandleStrategyContext context = HandleStrategyContext.getInstance();
        ServerTransactionHandler handler = createServerTransactionHandler();
        ChannelHandlerContext handlerContext = mock(ChannelHandlerContext.class);
        MessageProto.Message message = createMessage();
        context.handleMessageAccordingToAction(ActionType.ADD, handler, handlerContext, message);
        context.handleMessageAccordingToAction(ActionType.ADD_ASYNC, handler, handlerContext, message);
        context.handleMessageAccordingToAction(ActionType.ADD_STRONG, handler, handlerContext, message);
        context.handleMessageAccordingToAction(ActionType.APPLYFORSUBMIT, handler, handlerContext, message);
        context.handleMessageAccordingToAction(ActionType.APPLYFORSUBMIT_STRONG, handler, handlerContext, message);
        context.handleMessageAccordingToAction(ActionType.ASYNC_COMMIT, handler, handlerContext, message);
        context.handleMessageAccordingToAction(ActionType.CANCEL, handler, handlerContext, message);
        context.handleMessageAccordingToAction(ActionType.SUB_FAIL, handler, handlerContext, message);
        context.handleMessageAccordingToAction(ActionType.SUB_FAIL_STRONG, handler, handlerContext, message);
        context.handleMessageAccordingToAction(ActionType.SUB_SUCCESS, handler, handlerContext, message);
        context.handleMessageAccordingToAction(ActionType.SUB_SUCCESS_STRONG, handler, handlerContext, message);
        context.handleMessageAccordingToAction(ActionType.WHOLE_FAIL_STRONG_ACK, handler, handlerContext, message);
        context.handleMessageAccordingToAction(ActionType.WHOLE_SUCCESS_STRONG_ACK, handler, handlerContext, message);
    }
    
    private ServerTransactionHandler createServerTransactionHandler() {
        ServerTransactionHandler handler = mock(ServerTransactionHandler.class);
        when(handler.getChannelHandlerContext()).thenReturn(mock(ChannelHandlerContext.class));
        MessageAsyncCache messageAsyncCache = createMessageAsyncCache();
        when(handler.getMessageAsyncCacheProxy()).thenReturn(messageAsyncCache);
        when(handler.getMessageAsyncQueueProxy()).thenReturn(mock(MessageAsyncQueueProxy.class));
        when(handler.getMessageForSubmitAsyncCacheProxy()).thenReturn(mock(MessageForSubmitAsyncCache.class));
        when(handler.getMessageForSubmitSyncCacheProxy()).thenReturn(mock(MessageForSubmitSyncCache.class));
        MessageSyncCache messageSyncCache = createMessageSyncCache();
        when(handler.getMessageSyncCacheProxy()).thenReturn(messageSyncCache);
        ServerThreadLockCacheProxy serverThreadLockCacheProxy = createServerThreadLockCacheProxy();
        when(handler.getServerThreadLockCacheProxy()).thenReturn(serverThreadLockCacheProxy);
        ThreadPoolForServerProxy threadPoolForServerProxy = createThreadPoolForServerProxy();
        when(handler.getThreadPoolForServerProxy()).thenReturn(threadPoolForServerProxy);
        return handler;
    }
    
    private ThreadPoolForServerProxy createThreadPoolForServerProxy() {
        ThreadPoolForServerProxy result = mock(ThreadPoolForServerProxy.class);
        doNothing().when(result).execute(any());
        return result;
    }
    
    private ServerThreadLockCacheProxy createServerThreadLockCacheProxy() {
        ServerThreadLockCacheProxy result = mock(ServerThreadLockCacheProxy.class);
        LockAndConditionInterface lc = mock(LockAndConditionInterface.class);
        when(result.getIfPresent(any())).thenReturn(lc);
        return result;
    }
    
    private MessageSyncCache createMessageSyncCache() {
        MessageSyncCache result = mock(MessageSyncCache.class);
        TransactionMessageGroupInterface messageSync = mock(TransactionMessageGroupInterface.class);
        when(messageSync.toString()).thenReturn("");
        doNothing().when(result).putDependsOnCondition(any());
        when(result.get(any())).thenReturn(messageSync);
        return result;
    }
    
    private MessageAsyncCache createMessageAsyncCache() {
        TransactionMessageGroupInterface transactionMessageGroupInterface = mock(TransactionMessageGroupInterface.class);
        when(transactionMessageGroupInterface.getMemberSet()).thenReturn(new HashSet());
        when(transactionMessageGroupInterface.toString()).thenReturn("");
        MessageAsyncCache messageAsyncCache = mock(MessageAsyncCache.class);
        when(messageAsyncCache.get(any())).thenReturn(transactionMessageGroupInterface);
        return messageAsyncCache;
    }
    
    private MessageProto.Message createMessage() {
        MessageProto.Message.Builder builder = MessageProto.Message.newBuilder();
        JSONObject info = createJSONObject();
        builder.setInfo(info.toJSONString());
        return builder.build();
    }
    
    private JSONObject createJSONObject() {
        Set set = new HashSet<Long>();
        set.add(23L);
        JSONObject result = new JSONObject();
        result.put("groupId", "1231");
        result.put("groupMemberId", 23L);
        result.put("method", "");
        result.put("args", new Object[3]);
        result.put("groupMemberSet", set);
        result.put("memberId", 45L);
        result.put("url", "http://test-url");
        result.put("obj", "");
        result.put("httpAction", "post");
        return result;
    }
    
}
