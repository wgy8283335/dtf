package com.coconason.dtf.integration.test;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
            //process.destroy();
            if(i==0) {
                System.out.println("子进程正常完成");
            } else {
                System.out.println("子进程异常结束");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void finalSynchronizeTest() {
    }

    private static void readStreamInfo(InputStream... inputStreams) {
        for (InputStream in : inputStreams) {
            new Thread(()->{
                try{
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String line = null;
                    while((line=br.readLine())!=null){
                        System.out.println("inputStream:"+line);
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


}
