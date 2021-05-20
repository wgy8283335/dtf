package com.dtf.manager.utils;

import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
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
 * @author wangguangyuan
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
        } catch (IOException e) {
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

    /**
     * Send put request with json parameters.
     *
     * @param url request url
     * @param json parameters
     * @param groupId group id
     * @return response in string
     */
    public static String doPutJson(final String url, final String json, final String groupId) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            HttpPut httpPut = new HttpPut(url);
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPut.setEntity(entity);
            httpPut.setHeader("groupInfo", groupId);
            response = httpClient.execute(httpPut);
            StatusLine statusLine = response.getStatusLine();
            int code = statusLine.getStatusCode();
            if (code == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (IOException e) {
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

    /**
     * Send delete request with json parameters.
     *
     * @param url request url
     * @param groupId group id
     * @return response in string
     */
    public static String doDelete(final String url, final String groupId) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            HttpDelete httpDelete = new HttpDelete(url);
            httpDelete.setHeader("groupInfo", groupId);
            response = httpClient.execute(httpDelete);
            StatusLine statusLine = response.getStatusLine();
            int code = statusLine.getStatusCode();
            if (code == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (IOException e) {
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

    /**
     * Send get request with json parameters.
     *
     * @param url request url
     * @param groupId group id
     * @return response in string
     */
    public static String doGet(final String url, final String groupId) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("groupInfo", groupId);
            response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int code = statusLine.getStatusCode();
            if (code == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (IOException e) {
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
