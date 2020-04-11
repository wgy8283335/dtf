package com.coconason.dtf.manager.service;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.manager.cache.MessageCacheInterface;
import com.coconason.dtf.manager.cache.MessageForSubmitSyncCache;
import com.coconason.dtf.manager.cache.MessageSyncCache;
import com.coconason.dtf.manager.message.TransactionMessageGroup;
import com.coconason.dtf.manager.message.TransactionMessageGroupInterface;
import com.coconason.dtf.manager.threadpools.ThreadPoolForServerProxy;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

public class CheckAndSubmitRunnableTest {

    @Test
    public void testRun() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        MessageCacheInterface messageSyncCacheProxy = createMessageSyncCacheProxy();
        MessageCacheInterface messageForSubmitSyncCacheProxy = createMessageForSubmitSyncCacheProxy();
        ExecutorService threadPoolForServerProxy = createThreadPoolForServerProxy();
        MessageProto.Message message = createMessage();
        executorService.execute(new CheckAndSubmitRunnable(message, MessageProto.Message.ActionType.ADD, null, 
                messageForSubmitSyncCacheProxy, messageSyncCacheProxy, null, threadPoolForServerProxy));
        Thread.sleep(1000);
        assertEquals("345", messageSyncCacheProxy.get("345").getGroupId());
    }
    
    private ExecutorService createThreadPoolForServerProxy() {
        ExecutorService result = mock(ThreadPoolForServerProxy.class);
        doNothing().when(result).execute(any());
        return result;
    }
    
    private MessageCacheInterface createMessageForSubmitSyncCacheProxy() {
        MessageForSubmitSyncCache result = new MessageForSubmitSyncCache();
        TransactionMessageGroupInterface transactionMessageGroup = new TransactionMessageGroup("345");
        result.put("345", transactionMessageGroup);
        return result;
    }
    
    private MessageCacheInterface createMessageSyncCacheProxy() {
        MessageSyncCache result = new MessageSyncCache();
        TransactionMessageGroupInterface transactionMessageGroup = new TransactionMessageGroup("345");
        result.put("345", transactionMessageGroup);
        return result;
    }
    
    private MessageProto.Message createMessage() {
        MessageProto.Message.Builder builder = MessageProto.Message.newBuilder();
        JSONObject map = new JSONObject();
        map.put("groupId", "345");
        builder.setInfo(map.toJSONString());
        return builder.build();
    }
    
}
