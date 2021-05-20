package com.dtf.client.core.nettyclient.protobufclient;

import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

public class NettyServerConfigurationTest {
    
    @Test
    public void testGetAttributes() throws IllegalAccessException, NoSuchFieldException {
        NettyServerConfiguration configuration = createNettyServerConfiguration();
        assertEquals("localhost", configuration.getHost());
        assertEquals(8291, configuration.getPort().intValue());
    }
    
    private NettyServerConfiguration createNettyServerConfiguration() throws IllegalAccessException, NoSuchFieldException {
        NettyServerConfiguration result = new NettyServerConfiguration();
        Field host = result.getClass().getDeclaredField("host");
        host.setAccessible(true);
        host.set(result, "localhost");
        Field port = result.getClass().getDeclaredField("port");
        port.setAccessible(true);
        port.set(result, 8291);
        return result;
    }
    
}
