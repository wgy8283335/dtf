package com.dtf.manager.service;

import com.dtf.common.protobuf.MessageProto.Message.ActionType;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SendShortMessageRunnableTest {
    
    @Test
    public void testRunWithAddSuccessAsync() {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new SendShortMessageRunnable("123", ActionType.ADD_SUCCESS_ASYNC, null));
    }
    
}
