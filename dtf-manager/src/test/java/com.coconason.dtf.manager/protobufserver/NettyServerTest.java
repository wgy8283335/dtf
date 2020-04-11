package com.coconason.dtf.manager.protobufserver;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertTrue;


public class NettyServerTest {

    static ExecutorService executorService = Executors.newFixedThreadPool(1);
    
    @BeforeClass
    public static void startNettyServer() {
        executorService.execute(new NettyRunnable());
    }
    
    @Test
    public void testStartServer() throws Exception {
        Thread.sleep(2000);
        assertTrue(NettyServer.isHealthy());
    }

    @AfterClass
    public static void shutdownNettyServer() {
        executorService.shutdown();
    }

    private static class NettyRunnable implements Runnable {
        @Override
        public void run() {
            try { 
                NettyServer.main();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
