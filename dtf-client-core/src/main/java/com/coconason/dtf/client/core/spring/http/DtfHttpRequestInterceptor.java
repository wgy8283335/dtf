package com.coconason.dtf.client.core.spring.http;

import com.coconason.dtf.client.core.beans.TransactionGroupInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @Author: Jason
 * @date: 2018/8/10-10:20
 */
@Component
public class DtfHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private Logger logger = LoggerFactory.getLogger(DtfHttpRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {

        TransactionGroupInfo transactionGroupInfo = TransactionGroupInfo.getCurrent();
        logger.debug("DTF-SpringCloud TxGroup info -> groupId:"+transactionGroupInfo.toString());
        if(transactionGroupInfo!=null) {
            httpRequest.getHeaders().add("groupInfo", transactionGroupInfo.toString());
        }

        ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest,bytes);

        TransactionGroupInfo responseTransactionGroupInfo = TransactionGroupInfo.parse(response.getHeaders().get("groupInfo").get(0));
        transactionGroupInfo.addMemebers(responseTransactionGroupInfo.getGroupMembers());

        return response;
    }
}
