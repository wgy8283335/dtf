package com.coconason.dtf.client.core.spring.http;

import com.coconason.dtf.client.core.beans.TransactionGroupInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @Author: Jason
 * @date: 2018/8/10-10:20
 */
public class DtfHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private Logger logger = LoggerFactory.getLogger(DtfHttpRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        TransactionGroupInfo transactionGroupInfo = TransactionGroupInfo.getCurrent();
        String groupId = transactionGroupInfo == null ? null : transactionGroupInfo.getGroupId();
        logger.debug("DTF-SpringCloud TxGroup info -> groupId:"+groupId);
        if(transactionGroupInfo!=null) {
            httpRequest.getHeaders().add("groupId", groupId);
        }
        ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest,bytes);
        Set<Integer> responseGroupMembers = (Set)response.getHeaders().get("groupMembers");
        String responseGroupId = response.getHeaders().get("groupId").get(0);
        transactionGroupInfo.setGroupMembers(responseGroupMembers);
        transactionGroupInfo.setGroupId(responseGroupId);
        return response;
    }
}
