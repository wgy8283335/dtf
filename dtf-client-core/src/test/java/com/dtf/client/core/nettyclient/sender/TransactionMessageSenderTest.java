package com.dtf.client.core.nettyclient.sender;

import com.alibaba.fastjson.JSONObject;
import com.dtf.client.core.beans.service.BaseTransactionServiceInfo;
import com.dtf.client.core.beans.service.TransactionServiceInfo;
import com.dtf.client.core.nettyclient.messagequeue.TransactionMessageQueueProxy;
import com.dtf.client.core.nettyclient.protobufclient.NettyService;
import com.dtf.common.protobuf.MessageProto;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Queue;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

public class TransactionMessageSenderTest {
    
    @Test
    public void testStartSendMessage() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        BaseTransactionServiceInfo info = new TransactionServiceInfo("1221", new JSONObject(), MessageProto.Message.ActionType.ADD);        
        Queue queue = new TransactionMessageQueueProxy();
        queue.add(info);
        NettyService service = mock(NettyService.class);
        doNothing().when(service).sendMsg(info);
        TransactionMessageSender sender = createTransactionMessageSender(queue, service);
        sender.startSendMessage();
    }
    
    private TransactionMessageSender createTransactionMessageSender(Queue queue, NettyService service) throws NoSuchFieldException, IllegalAccessException {
        TransactionMessageSender result = new TransactionMessageSender();
        Field queueField = result.getClass().getDeclaredField("queue");
        queueField.setAccessible(true);
        queueField.set(result, queue);
        Field serviceField = result.getClass().getDeclaredField("service");
        serviceField.setAccessible(true);
        serviceField.set(result, service);
        Field threadPoolForClientProxyField = result.getClass().getDeclaredField("threadPoolForClientProxy");
        threadPoolForClientProxyField.setAccessible(true);
        threadPoolForClientProxyField.set(result, Executors.newFixedThreadPool(5));
        return result;
    }
    
}
