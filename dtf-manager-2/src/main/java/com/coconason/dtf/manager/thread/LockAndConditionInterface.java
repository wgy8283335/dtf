package com.coconason.dtf.manager.thread;

import com.coconason.dtf.common.protobuf.MessageProto;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Jason
 * @date: 2018/10/2-10:53
 */
public interface LockAndConditionInterface {
    void await();

    boolean await(long milliseconds, TimeUnit timeUnit);

    void signal();

    void sendAndWaitForSignal(String groupId, MessageProto.Message.ActionType action, ChannelHandlerContext ctx, String msg) throws  Exception;

    void sendAndWaitForSignalOnce(String groupId, MessageProto.Message.ActionType action, ChannelHandlerContext ctx, String msg) throws  Exception;

    void sendAndWaitForSignalIfFailSendMessage(String groupId, MessageProto.Message.ActionType action, ChannelHandlerContext ctx, String msg) throws  Exception;

}
