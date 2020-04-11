package com.coconason.dtf.manager.service;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto.Message;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import com.coconason.dtf.manager.cache.MessageCacheInterface;
import com.coconason.dtf.manager.cache.MessageSyncCache;
import com.coconason.dtf.manager.message.TransactionMessageGroup;
import com.coconason.dtf.manager.message.TransactionMessageGroupInterface;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

public class CancelRunnableTest {
    
    @Test
    public void testRun() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        MessageCacheInterface messageSyncCacheProxy = createMessageSyncCacheProxy();
        Message message = createMessage();
        executorService.execute(new CancelRunnable(message, ActionType.CANCEL, null, messageSyncCacheProxy, null, null));
        Thread.sleep(1000);
        assertEquals("345", messageSyncCacheProxy.get("345").getGroupId());
    }
    
    private MessageCacheInterface createMessageSyncCacheProxy() {
        MessageSyncCache result = new MessageSyncCache();
        TransactionMessageGroupInterface transactionMessageGroup = new TransactionMessageGroup("345");
        result.put("345", transactionMessageGroup);
        return result;
    }
    
    private Message createMessage() {
        Message.Builder builder = Message.newBuilder();
        JSONObject map = new JSONObject();
        map.put("groupId", "345");
        builder.setInfo(map.toJSONString());
        return builder.build();
    }
    
}
