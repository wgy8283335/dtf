package com.coconason.dtf.manager.message;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TransactionMessageGroupAsyncTest {
    
    @Test
    public void testGetterAndSetter() {
        TransactionMessageGroupAsync message = new TransactionMessageGroupAsync("1637231");
        message.addMember("74343", "http://localhost", "", "get");
        assertEquals("1637231", message.getGroupId());
        assertEquals(1, message.getMemberSet().size());
    }
    
    @Test
    public void testEquals() {
        TransactionMessageGroupAsync message = new TransactionMessageGroupAsync("1637231");
        TransactionMessageGroupAsync message2 = new TransactionMessageGroupAsync("1637231");
        assertEquals(message, message2);
    }
    
}
