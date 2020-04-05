package com.coconason.dtf.client.core.spring.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * Restful request client in synchronous mode.
 * 
 * @Author: Jason
 */
@Component
public final class RestClient {
    
    @Autowired
    private ClientHttpRequestInterceptor dtfHttpRequestInterceptor;
    
    private RestTemplate restTemplate = new RestTemplate();
    
    /**
     * Send request in post method.
     * 
     * @param url http url
     * @param object parameter
     * @return result of post request
     */
    public String sendPost(final String url, final Object object) {
        restTemplate.setInterceptors(Collections.singletonList(dtfHttpRequestInterceptor));
        String result = restTemplate.postForObject(url, object, String.class);
        return result;
    }
    
    /**
     * Send request in get method.
     * 
     * @param url http url
     * @param object parameter
     * @return result of get request
     */
    public String sendGet(final String url, final Object object) {
        restTemplate.setInterceptors(Collections.singletonList(dtfHttpRequestInterceptor));
        String result = restTemplate.getForObject(url, String.class, object);
        return result;
    }
    
    /**
     * Send request in put method.
     *
     * @param url http url
     * @param request parameters in String
     * @param uriVariables uri variables
     * @return result of get request
     */
    public String sendPut(final String url, final String request, final Object uriVariables) {
        restTemplate.setInterceptors(Collections.singletonList(dtfHttpRequestInterceptor));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(request,headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String .class, uriVariables);
        String result = response.getBody();
        return result;
    }
    
    /**
     * Send request in delete method.
     *
     * @param url http url
     * @param request parameters in String
     * @param uriVariables uri variables
     * @return result of get request
     */
    public String sendDelete(final String url, final String request, final Object uriVariables) {
        restTemplate.setInterceptors(Collections.singletonList(dtfHttpRequestInterceptor));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(request,headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String .class, uriVariables);
        String result = response.getBody();
        return result;
    }
    
}
