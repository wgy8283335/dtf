package com.coconason.dtf.client.core.spring.client;

import com.coconason.dtf.client.core.beans.group.TransactionGroupInfo;
import org.junit.Test;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;

import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HeaderModifierAdviceTest {
    
    @Test
    public void testSupports() {
        HeaderModifierAdvice advice = new HeaderModifierAdvice();
        assertEquals(true, advice.supports(null, null));
    }
    
    @Test
    public void testBeforeBodyWrite() {
        TransactionGroupInfo.setCurrent(new TransactionGroupInfo("9898", 21L, new HashSet<Long>()));
        HeaderModifierAdvice advice = new HeaderModifierAdvice();
        ServletServerHttpRequest request = mock(ServletServerHttpRequest.class);
        ServletServerHttpResponse response = createResponse();
        advice.beforeBodyWrite(new Object(), null, null, null, request, response);
    }
    
    private ServletServerHttpResponse createResponse() {
        ServletServerHttpResponse result = mock(ServletServerHttpResponse.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
        when(httpServletResponse.getHeader("groupInfo")).thenReturn("985758445");
        when(result.getServletResponse()).thenReturn(httpServletResponse);
        return result;
    }
    
}
