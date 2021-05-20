package com.dtf.manager.utils;

import com.dtf.common.protobuf.MessageProto.Message.ActionType;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageSenderTest {
    
    @Test
    public void testSendMsg() {
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        when(ctx.writeAndFlush(any())).thenReturn(null);
        MessageSender.sendMsg("7382", ActionType.ADD_ASYNC, ctx);
    }
    
}
