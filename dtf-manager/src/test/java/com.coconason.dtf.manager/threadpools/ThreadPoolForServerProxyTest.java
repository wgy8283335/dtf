package com.dtf.manager.threadpools;

import com.dtf.manager.utils.PropertiesReader;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertTrue;

public class ThreadPoolForServerProxyTest {
    
    @Test
    public void testInitialize() throws Exception {
        InputStream configPath = ThreadPoolForServerProxyTest.class.getClassLoader().getResourceAsStream("config.properties");
        PropertiesReader propertiesReader = new PropertiesReader(configPath);
        ThreadPoolForServerProxy threadPool = ThreadPoolForServerProxy.initialize(propertiesReader);
        Integer number = 100;
        threadPool.execute(new TestRunnable(number));
        assertTrue(100==number);
    }
    
    private class TestRunnable implements Runnable {
        
        private Integer number;
        
        public TestRunnable(Integer number) {
            this.number = number;
        }
        
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            number++;
        }
    }
    
}
