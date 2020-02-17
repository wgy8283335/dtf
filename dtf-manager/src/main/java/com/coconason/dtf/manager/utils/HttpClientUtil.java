package com.coconason.dtf.manager.utils;

import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Http client utility.
 * 
 * @Author: Jason
 */
public final class HttpClientUtil {
    
    /**
     * Logger for HttpClientUtil.
     */
    private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
    
    /**
     * Send post request with json parameters.
     * 
     * @param url request url
     * @param json parameters
     * @param groupId group id
     * @return response in string
     */
    public static String doPostJson(final String url, final String json, final String groupId) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            HttpPost httpPost = new HttpPost(url);
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            httpPost.setHeader("groupInfo", groupId);
            response = httpClient.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            int code = statusLine.getStatusCode();
            if (code == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return resultString;
    }
}
