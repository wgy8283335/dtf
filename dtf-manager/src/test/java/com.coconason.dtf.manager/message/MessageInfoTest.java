package com.coconason.dtf.manager.message;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MessageInfoTest {
    
    @Test
    public void testConstructor() {
        long timeStamp = System.currentTimeMillis();
        MessageInfo info = new MessageInfo("1", true, "http://localhost", "", timeStamp, "get");
        assertNotNull(info);
        assertEquals("1", info.getGroupMemberId());
        assertEquals(true, info.isCommitted());
        assertEquals("http://localhost", info.getUrl());
        assertEquals("", info.getObj());
        assertEquals(timeStamp, info.getTimeStamp());
        assertEquals("get", info.getHttpAction());
    }
    
    @Test
    public void testEqual() {
        long timeStamp = System.currentTimeMillis();
        MessageInfo info = new MessageInfo("1", true, "http://localhost", "", timeStamp, "get");
        MessageInfo info2 = new MessageInfo("1", true, "http://localhost", "", timeStamp, "get");
        assertTrue(info.equals(info2));
    }
    
}
