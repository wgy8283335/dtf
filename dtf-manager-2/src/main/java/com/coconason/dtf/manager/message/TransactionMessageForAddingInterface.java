package com.coconason.dtf.manager.message;

import io.netty.channel.ChannelHandlerContext;

/**
 * @Author: Jason
 * @date: 2018/8/24-17:01
 */
public interface TransactionMessageForAddingInterface {

    String getGroupMemberId();

    ChannelHandlerContext getCtx();

    Boolean isCommitted();

    void setCommitted(boolean commited);

    String toString();
}
