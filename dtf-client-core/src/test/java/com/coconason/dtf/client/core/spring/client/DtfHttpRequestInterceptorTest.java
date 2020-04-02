package com.coconason.dtf.client.core.spring.client;

import com.coconason.dtf.client.core.beans.group.BaseTransactionGroupInfo;
import com.coconason.dtf.client.core.beans.group.TransactionGroupInfo;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DtfHttpRequestInterceptorTest {
    
    @Test
    public void testIntercept() throws IOException {
        Set set = new HashSet<Long>();
        set.add(1L);
        TransactionGroupInfo info = new TransactionGroupInfo("989", 1L, set);
        TransactionGroupInfo.setCurrent(info);
        ClientHttpRequestExecution clientHttpRequestExecution = mock(ClientHttpRequestExecution.class);
        HttpRequest request = createHttpRequest();
        byte[] bytes = new byte[10];
        ClientHttpResponse expectedResponse = createHttpResponse();
        when(clientHttpRequestExecution.execute(request,bytes)).thenReturn(expectedResponse);
        DtfHttpRequestInterceptor interceptor = new DtfHttpRequestInterceptor();
        ClientHttpResponse actual = interceptor.intercept(request, bytes, clientHttpRequestExecution);
        BaseTransactionGroupInfo groupInfo = TransactionGroupInfo.getCurrent();
        assertEquals(expectedResponse, actual);
        assertTrue(groupInfo.getGroupMembers().contains(77L));
        assertTrue(groupInfo.getGroupMembers().contains(9876L));
    }
    
    private HttpRequest createHttpRequest() {
        HttpRequest request = new HttpRequest() {
            @Override
            public String getMethodValue() {
                return null;
            }

            @Override
            public URI getURI() {
                return null;
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders result = new HttpHeaders();
                result.put("groupInfo", null);
                return result;
            }
        };
        return request;
    }
    
    private ClientHttpResponse createHttpResponse() {
        ClientHttpResponse result = new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return null;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return 0;
            }

            @Override
            public String getStatusText() throws IOException {
                return null;
            }

            @Override
            public void close() {

            }

            @Override
            public InputStream getBody() throws IOException {
                return null;
            }

            @Override
            public HttpHeaders getHeaders() {
                List<String> groupInfoList = new ArrayList<>();
                groupInfoList.add("123845-77-77-9876");
                groupInfoList.add("9876");
                HttpHeaders result = new HttpHeaders();
                result.put("groupInfo", groupInfoList);
                return result;
            }
        };
        return result;
    }
    
}
