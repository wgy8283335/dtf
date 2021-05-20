package com.dtf.manager.utils;

import com.dtf.manager.message.MessageInfo;
import com.dtf.manager.message.MessageInfoInterface;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class SetUtilTest {
    
    @Test
    public void testIsSetEqual() {
        Set set1 = new HashSet<String>();
        set1.add("1");
        Set set2 = new HashSet<String>();
        set2.add("1");
        assertTrue(SetUtil.isSetEqual(set1, set2));
    }

    @Test
    public void testSetTransfer() {
        Set<MessageInfoInterface> set1 = new HashSet<>();
        MessageInfo message = new MessageInfo("1", false, "http://localhost", "", System.currentTimeMillis(), "get");
        set1.add(message);
        Set set2 = SetUtil.setTransfer(set1);
        assertTrue(set2.contains("1"));
    }

}
