package com.coconason.dtf.manager.thread;

import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.manager.protobufserver.NettyServer;
import com.coconason.dtf.manager.utils.MessageSender;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @Author: Jason
 * @date: 2018/8/22-11:44
 */
public final class LockAndCondition implements LockAndConditionInterface {

    private static final Logger logger = LoggerFactory.getLogger(LockAndCondition.class);
    private Lock lock;
    private Condition condition;

    public LockAndCondition(Lock lock) {
        this.lock = lock;
        this.condition = lock.newCondition();
    }
    @Override
    public void await() {
        try {
            lock.lock();
            condition.await();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        } finally {
            lock.unlock();
        }
    }
    @Override
    public boolean await(long milliseconds, TimeUnit timeUnit) {
        boolean result = false;
        try {
            lock.lock();
            result = condition.await(milliseconds, timeUnit);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        } finally {
            lock.unlock();
        }
        return result;
    }
    @Override
    public void signal() {
        try {
            lock.lock();
            condition.signal();
        } finally {
            lock.unlock();
        }
    }
    @Override
    public void sendAndWaitForSignal(String groupId, MessageProto.Message.ActionType action, ChannelHandlerContext ctx, String msg) throws  Exception{
        MessageSender.sendMsg(groupId,action,ctx);
        boolean receivedSignal = await(10000, TimeUnit.MILLISECONDS);
        while(receivedSignal == false){
            boolean channelIsHealthy = NettyServer.isHealthy();
            if(channelIsHealthy){
                MessageSender.sendMsg(groupId,action,ctx);
                receivedSignal = await(60000, TimeUnit.MILLISECONDS);
            }else{
                //should write log.
                logger.error(msg+"\n"+"groupId:"+groupId+"\n"+"action:"+action);
                throw new Exception(msg);
            }
        }
    }
    @Override
    public void sendAndWaitForSignalOnce(String groupId, MessageProto.Message.ActionType action, ChannelHandlerContext ctx, String msg) throws  Exception{
        MessageSender.sendMsg(groupId,action,ctx);
        boolean receivedSignal = await(10000, TimeUnit.MILLISECONDS);
        if(receivedSignal == false){
            logger.error(msg+"\n"+"groupId:"+groupId+"\n"+"action:"+action);
            throw new Exception(msg);
        }
    }
    @Override
    public void sendAndWaitForSignalIfFailSendMessage(String groupId, MessageProto.Message.ActionType action, ChannelHandlerContext ctx, String msg) throws  Exception{
        MessageSender.sendMsg(groupId,action,ctx);
        boolean receivedSignal = await(10000, TimeUnit.MILLISECONDS);
        if(receivedSignal == false){
            logger.error(msg+"\n"+"groupId:"+groupId+"\n"+"action:"+action);
            MessageSender.sendMsg(groupId.substring(0,18), MessageProto.Message.ActionType.WHOLE_FAIL_STRONG,ctx);
            throw new Exception(msg);
        }
    }

}
