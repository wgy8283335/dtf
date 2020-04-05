package com.coconason.dtf.client.core.spring.client;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RestClientTest {
    
    @Test
    public void testSendGet() throws NoSuchFieldException, IllegalAccessException {
        Object object = new Object();
        RestClient restClient = createRestClient(object, null);
        String actual = restClient.sendGet("http://test-url", object);
        assertEquals("good result.", actual);
    }
    
    @Test
    public void testSendPost() throws NoSuchFieldException, IllegalAccessException {
        Object object = new Object();
        RestClient restClient = createRestClient(object, null);
        String actual = restClient.sendPost("http://test-url", object);
        assertEquals("good result.", actual);
    }
    
    @Test
    public void testSendPut() throws NoSuchFieldException, IllegalAccessException {
        Object object = new Object();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>("",headers);
        RestClient restClient = createRestClient(object, entity);
        String actual = restClient.sendPut("http://test-url", "", object);
        assertEquals("good result.", actual);
    }
    
    @Test
    public void testSendDelete() throws NoSuchFieldException, IllegalAccessException {
        Object object = new Object();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>("",headers);
        RestClient restClient = createRestClient(object, entity);
        String actual = restClient.sendDelete("http://test-url", "", object);
        assertEquals("good result.", actual);
    }
    
    private RestClient createRestClient(Object object, HttpEntity entity) throws NoSuchFieldException, IllegalAccessException {
        RestClient result = new RestClient();
        Field field = result.getClass().getDeclaredField("restTemplate");
        field.setAccessible(true);
        RestTemplate restTemplate = mock(RestTemplate.class);
        when(restTemplate.postForObject("http://test-url", object, String.class)).thenReturn("good result.");
        when(restTemplate.getForObject("http://test-url", String.class, object)).thenReturn("good result.");
        ResponseEntity<String> responseEntity = new ResponseEntity<String>(HttpStatus.OK);
        String body = "good result.";
        when(restTemplate.exchange("http://test-url", HttpMethod.PUT, entity, String.class, object)).thenReturn(new ResponseEntity<String>(body, HttpStatus.OK));
        when(restTemplate.exchange("http://test-url", HttpMethod.DELETE, entity, String.class, object)).thenReturn(new ResponseEntity<String>(body, HttpStatus.OK));
        field.set(result, restTemplate);
        return result;
    }
    
}
