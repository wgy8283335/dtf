package com.coconason.dtf.manager.cache;

import com.coconason.dtf.manager.message.MessageInfo;
import org.junit.Test;

import java.util.Queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MessageAsyncQueueProxyTest {
    
    @Test
    public void testConstructor() {
        Queue queue = new MessageAsyncQueueProxy();
        assertNotNull(queue);
    }
    
    @Test
    public void testAdd() {
        Queue queue = new MessageAsyncQueueProxy();
        queue.add(new MessageInfo("1", true, null, "", 1L, null));
        assertNotNull(queue.poll());
    }
    
    @Test
    public void testIsEmpty() {
        Queue queue = new MessageAsyncQueueProxy();
        assertEquals(true, queue.isEmpty());
    }
    
}
