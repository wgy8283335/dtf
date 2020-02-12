package com.coconason.dtf.client.core.spring.client;

import org.springframework.beans.factory.annotation.Autowired;
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
    
    /**
     * Send request in post method.
     * 
     * @param url http url
     * @param object parameter
     * @return result of post request
     */
    public String sendPost(final String url, final Object object) {
        RestTemplate restTemplate = new RestTemplate();
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
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(dtfHttpRequestInterceptor));
        String result = restTemplate.getForObject(url, String.class, object);
        return result;
    }
    
}
