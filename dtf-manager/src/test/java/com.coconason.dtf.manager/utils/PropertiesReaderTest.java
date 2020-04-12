package com.coconason.dtf.manager.utils;

import com.coconason.dtf.manager.threadpools.ThreadPoolForServerProxyTest;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class PropertiesReaderTest {
    
    @Test
    public void testGetProperty() throws Exception{
        InputStream configPath = ThreadPoolForServerProxyTest.class.getClassLoader().getResourceAsStream("config.properties");
        PropertiesReader reader = new PropertiesReader(configPath);
        assertEquals(100, reader.getProperty("corePoolSize"));
    }
    
}
