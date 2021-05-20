package com.dtf.client.core.spring.listener;

import com.dtf.client.core.nettyclient.protobufclient.NettyService;
import com.dtf.client.core.nettyclient.sender.MessageSenderInterface;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.mockito.Mockito.mock;

public class TransactionNettyListenerTest {
    
    @Test
    public void testOnApplicationEvent() throws NoSuchFieldException, IllegalAccessException {
        TransactionNettyListener listener = createTransactionNettyListener();
        listener.onApplicationEvent(null);
    }
    
    private TransactionNettyListener createTransactionNettyListener() throws NoSuchFieldException, IllegalAccessException {
        TransactionNettyListener transactionNettyListener = new TransactionNettyListener();
        Field nettyServiceField = transactionNettyListener.getClass().getDeclaredField("nettyService");
        nettyServiceField.setAccessible(true);
        NettyService nettyService = mock(NettyService.class);
        nettyServiceField.set(transactionNettyListener, nettyService);
        Field senderField = transactionNettyListener.getClass().getDeclaredField("sender");
        senderField.setAccessible(true);
        MessageSenderInterface sender = mock(MessageSenderInterface.class);
        senderField.set(transactionNettyListener, sender);
        return transactionNettyListener;
    }
}
