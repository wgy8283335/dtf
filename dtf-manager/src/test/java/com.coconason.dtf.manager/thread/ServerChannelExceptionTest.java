package com.dtf.manager.thread;

import org.junit.Test;

public class ServerChannelExceptionTest {

    @Test
    public void testServerChannelException() {
        ServerChannelException exception = new ServerChannelException("This is test mesage.");
    }
    
}
