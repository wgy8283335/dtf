package com.coconason.dtf.manager.utils;

import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.manager.protobufserver.NettyServer;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @Author: Jason
 * @date: 2018/8/22-11:44
 */
public class LockAndCondition {
    private Lock lock;
    private Condition condition;

    public LockAndCondition(Lock lock) {
        this.lock = lock;
        this.condition = lock.newCondition();
    }

    public void await() {
        try {
            lock.lock();
            condition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public boolean await(long milliseconds, TimeUnit timeUnit) {
        boolean result = false;
        try {
            lock.lock();
            result = condition.await(milliseconds, timeUnit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return result;
    }

    public void signal() {
        try {
            lock.lock();
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    public void sendAndWaitForSignal(String groupId, MessageProto.Message.ActionType action, ChannelHandlerContext ctx, String msg) throws  Exception{
        MessageSender.sendMsg(groupId,action,ctx);
        boolean receivedSignal = await(10000, TimeUnit.MILLISECONDS);
        if(receivedSignal == false){
            boolean channelIsHealthy = NettyServer.isHealthy();
            if(channelIsHealthy){
                MessageSender.sendMsg(groupId,action,ctx);
                boolean receivedSignal2 = await(10000, TimeUnit.MILLISECONDS);
                if(receivedSignal2 == false){
                    throw new Exception(msg);
                }
            }else{
                throw new Exception(msg);
            }
        }
    }
}
