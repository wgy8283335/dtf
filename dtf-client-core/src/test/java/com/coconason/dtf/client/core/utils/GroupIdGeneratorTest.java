package com.coconason.dtf.client.core.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GroupIdGeneratorTest {
    
    @Test
    public void assertGetStringId() {
        String actual = GroupIdGenerator.getStringId(109980921L, 1L);
        assertEquals(18, actual.length());
    }
}
