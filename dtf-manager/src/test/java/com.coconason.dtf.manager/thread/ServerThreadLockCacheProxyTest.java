package com.dtf.manager.thread;

import org.junit.Test;

import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.assertEquals;

public class ServerThreadLockCacheProxyTest {
    
    @Test
    public void testGetAndSet() {
        LockAndConditionInterface lc = new LockAndCondition(new ReentrantLock());
        ServerThreadLockCacheProxy cache = new ServerThreadLockCacheProxy();
        cache.put("123", lc);
        assertEquals(lc, cache.getIfPresent("123"));
    }
    
}
