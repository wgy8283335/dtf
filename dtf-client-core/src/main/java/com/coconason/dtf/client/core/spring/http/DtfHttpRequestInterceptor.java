package com.coconason.dtf.client.core.spring.http;

import com.coconason.dtf.client.core.beans.TransactionGroupInfo;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @Author: Jason
 * @date: 2018/8/10-10:20
 */
@Component
public class DtfHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {

        TransactionGroupInfo transactionGroupInfo = TransactionGroupInfo.getCurrent();
        if(transactionGroupInfo!=null) {
            httpRequest.getHeaders().add("groupInfo", transactionGroupInfo.toString());
        }
        ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest,bytes);
        List<String> groupInfoStringList = response.getHeaders().get("groupInfo");
        if(groupInfoStringList != null){
            TransactionGroupInfo responseTransactionGroupInfo = TransactionGroupInfo.parse(groupInfoStringList.get(0));
            transactionGroupInfo.addMemebers(responseTransactionGroupInfo.getGroupMembers());
        }
        return response;
    }
}
