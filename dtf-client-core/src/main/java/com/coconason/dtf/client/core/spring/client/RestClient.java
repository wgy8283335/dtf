package com.coconason.dtf.client.core.spring.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.*;

/**
 * @Author: Jason
 * @date: 2018/8/27-15:24
 */
@Component
public class RestClient {

    @Autowired
    private ClientHttpRequestInterceptor dtfHttpRequestInterceptor;

    public String sendPost(String url, Object object){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(dtfHttpRequestInterceptor));
        String result = restTemplate.postForObject(url,object,String.class);
        return result;
    }

    public String sendGet(String url, Object object){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(dtfHttpRequestInterceptor));
        String result = restTemplate.getForObject(url,String.class,object);
        return result;
    }
}
