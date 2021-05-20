package com.dtf.manager.utils;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class HttpClientUtilTest {
    
    @Test
    public void testDoPostJson() throws IOException {
        String response = HttpClientUtil.doPostJson("http://localhost", "{'name':'value'}", "123123");
        assertEquals("", response);
    }
    
    @Test
    public void testDoPutJson() throws IOException {
        String response = HttpClientUtil.doPutJson("http://localhost", "{'name':'value'}", "123123");
        assertEquals("", response);
    }
    
    @Test
    public void testDoDelete() throws IOException {
        String response = HttpClientUtil.doDelete("http://localhost", "123123");
        assertEquals("", response);
    }
    
    @Test
    public void testDoGet() throws IOException {
        String response = HttpClientUtil.doGet("http://localhost", "123123");
        assertEquals("", response);
    }
    
}
