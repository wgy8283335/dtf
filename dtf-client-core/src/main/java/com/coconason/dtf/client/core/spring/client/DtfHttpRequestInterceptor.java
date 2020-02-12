package com.coconason.dtf.client.core.spring.client;

import com.coconason.dtf.client.core.beans.BaseTransactionGroupInfo;
import com.coconason.dtf.client.core.beans.TransactionGroupInfo;
import com.coconason.dtf.client.core.beans.TransactionGroupInfoFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Http request interceptor.
 * 
 * @Author: Jason
 */
@Component
final class DtfHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    
    /**
     * Interceptor for http request.
     * Add group information into a request header.
     * After get response, extract group information from response and add it into transaction group information.
     * 
     * @param httpRequest http request
     * @param bytes byte array
     * @param clientHttpRequestExecution client http request execution
     * @return client http response
     */
    @Override
    public ClientHttpResponse intercept(final HttpRequest httpRequest, final byte[] bytes, final ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        BaseTransactionGroupInfo transactionGroupInfo = TransactionGroupInfo.getCurrent();
        if (null != transactionGroupInfo) {
            httpRequest.getHeaders().add("groupInfo", transactionGroupInfo.toString());
        }
        ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest, bytes);
        List<String> groupInfoStringList = response.getHeaders().get("groupInfo");
        if (null != groupInfoStringList) {
            BaseTransactionGroupInfo responseTransactionGroupInfo = TransactionGroupInfoFactory.getInstanceByParsingString(groupInfoStringList.get(0));
            transactionGroupInfo.addMembers(responseTransactionGroupInfo.getGroupMembers());
        }
        return response;
    }
    
}
