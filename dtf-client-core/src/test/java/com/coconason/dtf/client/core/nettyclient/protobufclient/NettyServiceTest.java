package com.coconason.dtf.client.core.nettyclient.protobufclient;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.client.core.beans.service.TransactionServiceInfo;
import com.coconason.dtf.client.core.threadpools.ThreadPoolForClientProxy;
import com.coconason.dtf.common.protobuf.MessageProto;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

public class NettyServiceTest {
    
    @Test
    public void testStart() throws NoSuchFieldException, IllegalAccessException {
        NettyService service = createNettyService();
        service.start();
    }
    
    @Test
    public void testClose() throws NoSuchFieldException, IllegalAccessException {
        NettyService service = createNettyService();
        service.close();
    }
    
    @Test
    public void testIsHealthy() throws NoSuchFieldException, IllegalAccessException {
        NettyService service = createNettyService();
        assertEquals(false, service.isHealthy());
    }
    
    @Test
    public void testSendMessage() throws NoSuchFieldException, IllegalAccessException {
        NettyService service = createNettyService();
        service.sendMsg(new TransactionServiceInfo("8495", new JSONObject(), MessageProto.Message.ActionType.ADD));
    }
    
    private NettyService createNettyService() throws NoSuchFieldException, IllegalAccessException {
        NettyService service = new NettyService();
        Field clientTransactionHandlerField = service.getClass().getDeclaredField("clientTransactionHandler");
        Field heartBeatReqHandlerField = service.getClass().getDeclaredField("heartBeatReqHandler");
        Field loginAuthReqHandlerField = service.getClass().getDeclaredField("loginAuthReqHandler");
        Field threadPoolForClientProxyField = service.getClass().getDeclaredField("threadPoolForClientProxy");
        Field nettyServerConfigurationField = service.getClass().getDeclaredField("nettyServerConfiguration");
        clientTransactionHandlerField.setAccessible(true);
        heartBeatReqHandlerField.setAccessible(true);
        loginAuthReqHandlerField.setAccessible(true);
        threadPoolForClientProxyField.setAccessible(true);
        nettyServerConfigurationField.setAccessible(true);
        ClientTransactionHandler clientTransactionHandler = mock(ClientTransactionHandler.class);
        doNothing().when(clientTransactionHandler).sendMsg(any());
        HeartBeatReqHandler heartBeatReqHandler = mock(HeartBeatReqHandler.class);
        LoginAuthReqHandler loginAuthReqHandler = mock(LoginAuthReqHandler.class);
        ThreadPoolForClientProxy pool = mock(ThreadPoolForClientProxy.class);
        NettyServerConfiguration nettyServerConfiguration = mock(NettyServerConfiguration.class);
        clientTransactionHandlerField.set(service, clientTransactionHandler);
        heartBeatReqHandlerField.set(service, heartBeatReqHandler);
        loginAuthReqHandlerField.set(service, loginAuthReqHandler);
        threadPoolForClientProxyField.set(service, pool);
        nettyServerConfigurationField.set(service, nettyServerConfiguration);
        return service;
    }
    
}
