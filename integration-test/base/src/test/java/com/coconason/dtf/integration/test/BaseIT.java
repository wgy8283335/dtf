package com.coconason.dtf.integration.test;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.integration.test.entity.Course;
import com.coconason.dtf.integration.test.entity.DemoResult;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class BaseIT {
    
    @BeforeClass
    public static void initialization() {
        try {
            String filePath = Thread.currentThread().getContextClassLoader().getResource("initialization-demo.sh").getPath();
            Process process = Runtime.getRuntime().exec(filePath);
            InputStream errorStream = process.getErrorStream();
            InputStream inputStream = process.getInputStream();
            readStreamInfo(errorStream, inputStream);
            int i = process.waitFor();
            if(i==0) {
                System.out.println("Initialization process success");
            } else {
                System.out.println("Initialization process failure");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void finalSynchronizeTest() {
//        Course course = new Course();
//        course.setC(new Random().nextInt());
//        course.setCname("math");
//        course.setT(new Random().nextInt());
//        sendPost("http://localhost:8081/add_course_info", course);
    }
    
    @Test
    public void strongSynchronizeTest() {
//        Course course = new Course();
//        course.setC(new Random().nextInt());
//        course.setCname("math");
//        course.setT(new Random().nextInt());
//        sendPost("http://localhost:8081/add_course_info_strong", course);
    }
    
    @Test
    public void assertSuccessAsynchronizeTest() {
        Course course = new Course();
        Integer id = new Random().nextInt();
        course.setC(id);
        course.setCname("math");
        course.setT(id);
        String resultPost = sendPost("http://localhost:8081/add_course_info_async", course);
        try{
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String resultGetCourse = sendGet("http://localhost:8081/get_course?id="+id);
        String resultGetSC = sendGet("http://localhost:8083/get_sc?id="+id);
        String resultGetTeacher = sendGet("http://localhost:8082/get_teacher?id="+id);
        assertThat(checkResult(resultPost),is(true));
        assertThat(checkResult(resultGetCourse),is(true));
        assertThat(checkResult(resultGetSC),is(true));
        assertThat(checkResult(resultGetTeacher),is(true));
    }
    
    @Test
    public void assertFailureAsynchronizeTest() {
        Course course = new Course();
        Integer id = new Random().nextInt();
        course.setC(null);
        course.setCname("math");
        course.setT(id);
        String resultPost = sendPost("http://localhost:8081/add_course_info_async", course);
        try{
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String resultGetSC = sendGet("http://localhost:8083/get_sc?id="+id);
        String resultGetTeacher = sendGet("http://localhost:8082/get_teacher?id="+id);
        assertThat(checkResult(resultPost),is(false));
        assertThat(checkResult(resultGetSC),is(false));
        assertThat(checkResult(resultGetTeacher),is(false));
    }
    
    @AfterClass
    public static void destruction() {
        try {
            Thread.sleep(5000);
            String filePath = Thread.currentThread().getContextClassLoader().getResource("destruction-demo.sh").getPath();
            Process process = Runtime.getRuntime().exec(filePath);
            InputStream errorStream = process.getErrorStream();
            InputStream inputStream = process.getInputStream();
            readStreamInfo(errorStream, inputStream);
            int i = process.waitFor();
            if(i == 0) {
                System.out.println("Destruction process success");
            } else {
                System.out.println("Destruction process failure");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void readStreamInfo(InputStream... inputStreams) {
        for (InputStream in : inputStreams) {
            new Thread(()->{
                try{
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String line = null;
                    while((line = br.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
    
    private String sendPost(final String url, final Object object) {
        RestTemplate restTemplate = new RestTemplate();
        String result = null;
        try {
            result = restTemplate.postForObject(url, object, String.class);
        } catch (RestClientException e) {
            return new DemoResult(500, "", null).toString();
        }
        if(null == result){
            return new DemoResult(500, "", null).toString();
        }
        return result;
    }
    
    private String sendGet(final String url) {
        RestTemplate restTemplate = new RestTemplate();
        String result = null;
        try {
            result = restTemplate.getForObject(url, String.class);
        } catch (RestClientException e) {
            return new DemoResult(500, "", null).toString();
        }
        if(null == result){
            return new DemoResult(500, "", null).toString();
        }
        return result;
    }
    
    private boolean checkResult(final String httpResult) {
        Object object = JSONObject.parse(httpResult);
        JSONObject demoResult = (JSONObject)object;
        return "200".equals(demoResult.get("code").toString());
    }
    
}
