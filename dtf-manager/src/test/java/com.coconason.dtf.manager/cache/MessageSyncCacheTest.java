package com.dtf.manager.cache;

import com.dtf.manager.message.TransactionMessageForAdding;
import com.dtf.manager.message.TransactionMessageGroup;
import com.dtf.manager.message.TransactionMessageGroupInterface;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class MessageSyncCacheTest {
    
    @Test
    public void testPutAndGet() {
        MessageSyncCache cache = new MessageSyncCache();
        TransactionMessageGroup group = new TransactionMessageGroup("828746102");
        cache.put("12", group);
        assertEquals(group, cache.get("12"));
    }
    
    @Test
    public void testPutDependsOnCondition() {
        MessageSyncCache cache = new MessageSyncCache();
        Object obj = new Object();
        TransactionMessageGroup group = new TransactionMessageGroup("828746102");
        group.addMemberToGroup(new TransactionMessageForAdding("1", null, null, null));
        cache.put("828746102", group);
        TransactionMessageGroup group2 = new TransactionMessageGroup("828746102");
        group2.addMemberToGroup(new TransactionMessageForAdding("2", null, null, null));
        cache.putDependsOnCondition(group2);
        TransactionMessageGroupInterface actual = cache.get("828746102");
        Set<String> excepted = new HashSet<>();
        excepted.add("1");
        excepted.add("2");
        assertEquals(excepted, actual.getMemberSet());
    }
    
    @Test
    public void testGetSize() {
        MessageSyncCache cache = new MessageSyncCache();
        TransactionMessageGroup group = new TransactionMessageGroup("828746102");
        cache.put("12", group);
        assertEquals(1, cache.getSize());
    }
    
    @Test 
    public void testInvalidate() {
        MessageSyncCache cache = new MessageSyncCache();
        TransactionMessageGroup group = new TransactionMessageGroup("828746102");
        cache.put("12", group);
        cache.invalidate("12");
        assertEquals(0, cache.getSize());
    }
    
}
