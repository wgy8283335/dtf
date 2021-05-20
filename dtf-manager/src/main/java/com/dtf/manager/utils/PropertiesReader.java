package com.dtf.manager.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Reader of properties.
 * 
 * @author wangguangyuan
 */
public final class PropertiesReader {
    
    /**
     * Properties.
     */
    private Properties properties;
    
    /**
     * Load file to initialize properties.
     * 
     * @param filePath path of file
     * @throws Exception exception
     */
    public PropertiesReader(final String filePath) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
        properties = new Properties();
        properties.load(bufferedReader);
    }
    
    /**
     * Read input stream to initialize properties.
     *
     * @param stream input stream of file
     * @throws Exception exception
     */
    public PropertiesReader(final InputStream stream) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        properties = new Properties();
        properties.load(bufferedReader);
    }
    
    /**
     * Get property by key.
     * 
     * @param key key.
     * @return property.
     */
    public String getProperty(final String key) {
        return properties.getProperty(key);
    }
    
}
