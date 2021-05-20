package com.dtf.manager.message;

import io.netty.channel.ChannelHandlerContext;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class TransactionMessageGroupTest {
    
    @Test
    public void testGetterAndSetter() {
        TransactionMessageGroup group = new TransactionMessageGroup("95865");
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        group.setCtxForSubmitting(ctx);
        group.setGroupId("123");
        TransactionMessageForAdding message = new TransactionMessageForAdding("1", ctx, "", "");
        group.addMemberToGroup(message);
        assertEquals(ctx, group.getCtx());
        assertEquals("123", group.getGroupId());
        assertEquals(message, group.getMemberList().get(0));
        assertTrue(group.getMemberSet().contains(message.getGroupMemberId()));
    }
    
}
