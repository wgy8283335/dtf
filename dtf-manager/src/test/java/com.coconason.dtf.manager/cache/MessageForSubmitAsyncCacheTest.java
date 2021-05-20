package com.dtf.manager.cache;

import com.dtf.manager.message.TransactionMessageForSubmit;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MessageForSubmitAsyncCacheTest {
    
    @Test
    public void testPutAndGet() {
        MessageForSubmitAsyncCache cache = new MessageForSubmitAsyncCache();
        TransactionMessageForSubmit group = new TransactionMessageForSubmit("828746102", null);
        cache.put("12", group);
        assertEquals(group, cache.get("12"));
    }
    
    @Test
    public void testGetSize() {
        MessageForSubmitAsyncCache cache = new MessageForSubmitAsyncCache();
        TransactionMessageForSubmit group = new TransactionMessageForSubmit("828746102", null);
        cache.put("12", group);
        assertEquals(1, cache.getSize());
    }
    
    @Test
    public void testInvalidate() {
        MessageForSubmitAsyncCache cache = new MessageForSubmitAsyncCache();
        TransactionMessageForSubmit group = new TransactionMessageForSubmit("828746102", null);
        cache.put("12", group);
        cache.invalidate("12");
        assertEquals(0, cache.getSize());
    }
    
}
