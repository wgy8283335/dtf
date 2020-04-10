package com.coconason.dtf.manager.message;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class TransactionMessageForSubmitTest {
    
    @Test
    public void testGetterAndSetter() {
        Set set = createSet();
        TransactionMessageForSubmit transactionMessageForSubmit = new TransactionMessageForSubmit("2839232", set);
        transactionMessageForSubmit.setGroupId("83843");
        assertEquals("83843", transactionMessageForSubmit.getGroupId());
        assertEquals(set, transactionMessageForSubmit.getMemberSet());
    }
    
    private Set createSet() {
        Set result = new HashSet<>();
        result.add("1");
        result.add("2");
        return result;
    }
    
}
