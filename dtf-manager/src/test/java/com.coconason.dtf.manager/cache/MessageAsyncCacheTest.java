package com.coconason.dtf.manager.cache;

import com.coconason.dtf.manager.message.MessageInfo;
import com.coconason.dtf.manager.message.TransactionMessageGroup;
import com.coconason.dtf.manager.message.TransactionMessageGroupAsync;
import com.coconason.dtf.manager.message.TransactionMessageGroupInterface;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class MessageAsyncCacheTest {
    
    @Test
    public void testPutAndGet() {
        MessageAsyncCache cache = new MessageAsyncCache();
        TransactionMessageGroupAsync group = new TransactionMessageGroupAsync("828746102");
        cache.put("12", group);
        assertEquals(group, cache.get("12"));
    }
    
    @Test
    public void testPutDependsOnCondition() {
        MessageAsyncCache cache = new MessageAsyncCache();
        Object obj = new Object();
        TransactionMessageGroupAsync group = new TransactionMessageGroupAsync("828746102");
        group.addMember("1", "http://localhost", obj, "post");
        cache.put("828746102", group);
        TransactionMessageGroupAsync group2 = new TransactionMessageGroupAsync("828746102");
        group2.addMember("2", "http://localhost", obj, "get");
        cache.putDependsOnCondition(group2);
        TransactionMessageGroupInterface actual = cache.get("828746102");
        Set<MessageInfo> excepted = new HashSet<>();
        excepted.add(new MessageInfo("1", false, "http://localhost", obj, System.currentTimeMillis(), "post"));
        excepted.add(new MessageInfo("2", false, "http://localhost", obj, System.currentTimeMillis(), "get"));
        assertEquals(excepted, actual.getMemberSet());
    }
    
    @Test
    public void testGetSize() {
        MessageAsyncCache cache = new MessageAsyncCache();
        TransactionMessageGroupAsync group = new TransactionMessageGroupAsync("828746102");
        cache.put("12", group);
        assertEquals(1, cache.getSize());
    }
    
    @Test 
    public void testInvalidate() {
        MessageAsyncCache cache = new MessageAsyncCache();
        TransactionMessageGroupAsync group = new TransactionMessageGroupAsync("828746102");
        cache.put("12", group);
        cache.invalidate("12");
        assertEquals(0, cache.getSize());
    }
    
}
