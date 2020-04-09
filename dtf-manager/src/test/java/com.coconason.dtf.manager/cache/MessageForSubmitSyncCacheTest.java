package com.coconason.dtf.manager.cache;

import com.coconason.dtf.manager.message.TransactionMessageForSubmit;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MessageForSubmitSyncCacheTest {

    @Test
    public void testPutAndGet() {
        MessageForSubmitSyncCache cache = new MessageForSubmitSyncCache();
        TransactionMessageForSubmit group = new TransactionMessageForSubmit("828746102", null);
        cache.put("12", group);
        assertEquals(group, cache.get("12"));
    }

    @Test
    public void testGetSize() {
        MessageForSubmitSyncCache cache = new MessageForSubmitSyncCache();
        TransactionMessageForSubmit group = new TransactionMessageForSubmit("828746102", null);
        cache.put("12", group);
        assertEquals(1, cache.getSize());
    }

    @Test
    public void testInvalidate() {
        MessageForSubmitSyncCache cache = new MessageForSubmitSyncCache();
        TransactionMessageForSubmit group = new TransactionMessageForSubmit("828746102", null);
        cache.put("12", group);
        cache.invalidate("12");
        assertEquals(0, cache.getSize());
    }

}
