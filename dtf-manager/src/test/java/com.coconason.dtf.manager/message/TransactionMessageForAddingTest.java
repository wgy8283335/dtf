package com.dtf.manager.message;

import io.netty.channel.ChannelHandlerContext;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class TransactionMessageForAddingTest {
    
    @Test
    public void testGetterAndSetter() {
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        TransactionMessageForAddingInterface message = new TransactionMessageForAdding("1", ctx, "", "");
        message.setCommitted(true);
        assertEquals("1", message.getGroupMemberId());
        assertEquals(ctx, message.getCtx());
        assertEquals(true, message.isCommitted());
    }
    
}
