package com.coconason.dtf.manager.service;

import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import com.coconason.dtf.manager.thread.ServerThreadLockCacheProxy;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

public class SendMessgeRunnableTest {
    
    @Test
    public void testRunWithApproveSubmit() {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new SendMessageRunnable("123", ActionType.APPROVESUBMIT, null,null, createServerThreadLockCacheProxy()));
    }
    
    @Test
    public void testRunWithApproveSubmitStrong() {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new SendMessageRunnable("123", ActionType.APPROVESUBMIT_STRONG, null,null, createServerThreadLockCacheProxy()));
    }

    @Test
    public void testRunWithCancel() {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new SendMessageRunnable("123", ActionType.CANCEL, null,null, createServerThreadLockCacheProxy()));
    }
    
    private ServerThreadLockCacheProxy createServerThreadLockCacheProxy() {
        ServerThreadLockCacheProxy result = mock(ServerThreadLockCacheProxy.class);
        doNothing().when(result).put(any(), any());
        return result;
    }
    
}
