package com.coconason.dtf.manager.thread;

import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LockAndConditionTest {
    
    @Test
    public void testAwait() {
        ReentrantLock reentrantLock = new ReentrantLock();
        LockAndCondition lc = new LockAndCondition(reentrantLock);
        ExecutorService threadPool = Executors.newFixedThreadPool(1);
        threadPool.execute(new TestRunnable(lc));
        lc.await();
    }
    
    @Test
    public void testAwaitWithinTime() {
        ReentrantLock reentrantLock = new ReentrantLock();
        LockAndCondition lc = new LockAndCondition(reentrantLock);
        ExecutorService threadPool = Executors.newFixedThreadPool(1);
        threadPool.execute(new TestRunnable(lc));
        lc.await(2000, TimeUnit.MILLISECONDS);
    }
    
    @Test
    public void testAwaitWithTimeOut() {
        ReentrantLock reentrantLock = new ReentrantLock();
        LockAndCondition lc = new LockAndCondition(reentrantLock);
        assertFalse(lc.await(500, TimeUnit.MILLISECONDS));
    }
    
    @Test
    public void testSendAndWaitForSignal() throws ServerChannelException {
        ReentrantLock reentrantLock = new ReentrantLock();
        LockAndCondition lc = new LockAndCondition(reentrantLock);
        ExecutorService threadPool = Executors.newFixedThreadPool(1);
        threadPool.execute(new TestRunnable(lc));
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        when(ctx.writeAndFlush(any())).thenReturn(null);
        lc.sendAndWaitForSignal("1", ActionType.APPROVESUBMIT, ctx, "");
    }
    
    @Test
    public void testSendAndWaitForSignalOnce() throws ServerChannelException {
        ReentrantLock reentrantLock = new ReentrantLock();
        LockAndCondition lc = new LockAndCondition(reentrantLock);
        ExecutorService threadPool = Executors.newFixedThreadPool(1);
        threadPool.execute(new TestRunnable(lc));
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        when(ctx.writeAndFlush(any())).thenReturn(null);
        lc.sendAndWaitForSignalOnce("1", ActionType.CANCEL, ctx, "");
    }
    
    @Test
    public void testSendAndWaitForSignalIfFailSendMessage() throws ServerChannelException {
        ReentrantLock reentrantLock = new ReentrantLock();
        LockAndCondition lc = new LockAndCondition(reentrantLock);
        ExecutorService threadPool = Executors.newFixedThreadPool(1);
        threadPool.execute(new TestRunnable(lc));
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        when(ctx.writeAndFlush(any())).thenReturn(null);
        lc.sendAndWaitForSignalIfFailSendMessage("1", ActionType.APPROVESUBMIT_STRONG, ctx, "");
    }
    
    private class TestRunnable implements Runnable {
        
        private LockAndCondition lc;
        
        public TestRunnable(LockAndCondition lc) {
            this.lc = lc;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lc.signal();
        }
    }
    
}
