package com.coconason.dtf.manager.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;

/**
 * @Author: Jason
 * @date: 2018/9/19-10:50
 */
public class PropertiesReader {
    private Properties properties;
//    private BufferedReader bufferedReader;

    public PropertiesReader(String filePath) throws Exception{
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
        properties = new Properties();
        properties.load(bufferedReader);
    }

    public String getProperty(String key){
        return properties.getProperty(key);
    }

}
