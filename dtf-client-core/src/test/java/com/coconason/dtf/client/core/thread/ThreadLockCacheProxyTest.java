package com.coconason.dtf.client.core.thread;

import com.coconason.dtf.client.core.dbconnection.OperationType;
import org.junit.Test;

import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ThreadLockCacheProxyTest {
    
    @Test
    public void assertConstructor() {
        ThreadLockCacheProxy threadLockCacheProxy = new ThreadLockCacheProxy();
        assertNotNull(threadLockCacheProxy);
    }
    
    @Test
    public void assertPut() {
        ThreadLockCacheProxy threadLockCacheProxy = new ThreadLockCacheProxy();
        ClientLockAndConditionInterface value = new ClientLockAndCondition(new ReentrantLock(), OperationType.DEFAULT);
        threadLockCacheProxy.put("testKey", value);
        ClientLockAndConditionInterface actual = threadLockCacheProxy.getIfPresent("testKey");
        assertEquals(value, actual);
    }
    
    @Test
    public void assertGetIfPresent() {
        ThreadLockCacheProxy threadLockCacheProxy = new ThreadLockCacheProxy();
        ClientLockAndConditionInterface value = new ClientLockAndCondition(new ReentrantLock(), OperationType.DEFAULT);
        threadLockCacheProxy.put("testKey", value);
        ClientLockAndConditionInterface actual = threadLockCacheProxy.getIfPresent("testKey");
        assertEquals(value, actual);
    }
    
    @Test
    public void assertGetIfPresentWithWrongKey() {
        ThreadLockCacheProxy threadLockCacheProxy = new ThreadLockCacheProxy();
        ClientLockAndConditionInterface value = new ClientLockAndCondition(new ReentrantLock(), OperationType.DEFAULT);
        threadLockCacheProxy.put("testKey", value);
        ClientLockAndConditionInterface actual = threadLockCacheProxy.getIfPresent("testKey1");
        assertEquals(null, actual);
    }
    
}
