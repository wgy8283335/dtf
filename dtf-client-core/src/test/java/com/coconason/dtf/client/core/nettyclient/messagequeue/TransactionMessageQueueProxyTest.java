package com.coconason.dtf.client.core.nettyclient.messagequeue;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.client.core.beans.service.BaseTransactionServiceInfo;
import com.coconason.dtf.client.core.beans.service.TransactionServiceInfo;
import com.coconason.dtf.common.protobuf.MessageProto;
import org.junit.Test;

import java.util.Queue;

import static org.junit.Assert.assertEquals;

public class TransactionMessageQueueProxyTest {
    
    @Test
    public void testIsEmpty() {
        Queue queue = new TransactionMessageQueueProxy();
        assertEquals(true, queue.isEmpty());
    }
    
    @Test
    public void testAddPoll() {
        Queue queue = new TransactionMessageQueueProxy();
        BaseTransactionServiceInfo serviceInfo = new TransactionServiceInfo("1234", new JSONObject(), MessageProto.Message.ActionType.ADD);
        queue.add(serviceInfo);
        assertEquals(serviceInfo, queue.poll());
    }
    
}
