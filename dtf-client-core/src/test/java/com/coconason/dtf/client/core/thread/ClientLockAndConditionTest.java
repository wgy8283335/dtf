package com.coconason.dtf.client.core.thread;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.client.core.beans.service.BaseTransactionServiceInfo;
import com.coconason.dtf.client.core.beans.service.TransactionServiceInfo;
import com.coconason.dtf.client.core.dbconnection.OperationType;
import com.coconason.dtf.client.core.nettyclient.protobufclient.NettyService;
import com.coconason.dtf.common.protobuf.MessageProto;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class ClientLockAndConditionTest {
    
    @Test
    public void assertConstructor() {
        ClientLockAndCondition lc = createLC();
        assertNotNull(lc);
    }
    
    @Test
    public void assertGetState() {
        ClientLockAndCondition lc = createLC();
        OperationType state = lc.getState();
        assertEquals(OperationType.DEFAULT, state);
    }
    
    @Test
    public void assertSetState() {
        ClientLockAndCondition lc = createLC();
        lc.setState(OperationType.COMMIT);
        OperationType state = lc.getState();
        assertEquals(OperationType.COMMIT, state);
    }
    
    @Test
    public void assertAwaitWithTimeout() {
        ClientLockAndCondition lc = createLC();
        boolean actual = lc.await(1000, TimeUnit.MILLISECONDS);
        assertFalse(actual);
    }
    
    @Test
    public void assertAwaitWithSignal() {
        ClientLockAndCondition lc = createLC();
        sendSignal(lc);
        boolean actual = lc.await(1000, TimeUnit.MILLISECONDS);
        assertTrue(actual);
    }
    
    @Test(expected = Exception.class)
    public void assertAwaitLimitedTimeWithTimeout() throws Exception {
        ClientLockAndCondition lc = createLC();
        NettyService nettyService = mock(NettyService.class);
        BaseTransactionServiceInfo serviceInfo = new TransactionServiceInfo("1", new JSONObject(), MessageProto.Message.ActionType.ADD);
        lc.awaitLimitedTime(nettyService, serviceInfo,
        "await timeout", 1000, TimeUnit.MILLISECONDS);
    }
    
    @Test
    public void assertAwaitLimitedTimeWithinTime() throws Exception {
        ClientLockAndCondition lc = createLC();
        NettyService nettyService = mock(NettyService.class);
        BaseTransactionServiceInfo serviceInfo = new TransactionServiceInfo("1", new JSONObject(), MessageProto.Message.ActionType.ADD);
        sendSignal(lc);
        lc.awaitLimitedTime(nettyService, serviceInfo,
                "await timeout", 1000, TimeUnit.MILLISECONDS);
    }
    
    private ClientLockAndCondition createLC() {
        ClientLockAndCondition result = new ClientLockAndCondition(new ReentrantLock(), OperationType.DEFAULT);
        return result;
    }
    
    private void sendSignal(ClientLockAndCondition lc) {
        ExecutorService threadPool = Executors.newFixedThreadPool(1);
        threadPool.execute(new SignalRunnable(lc));
    }
    
    private class SignalRunnable implements Runnable {
        
        ClientLockAndCondition lc;
        
        public SignalRunnable(ClientLockAndCondition lc) {
            this.lc = lc;
        }
        
        @Override
        public void run() {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lc.signal();
        }
    }
    
}
