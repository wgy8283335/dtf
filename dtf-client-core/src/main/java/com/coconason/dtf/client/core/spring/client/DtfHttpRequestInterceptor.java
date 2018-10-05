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
 * @Author: Jason
 * @date: 2018/8/10-10:20
 */
@Component
final class DtfHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {

        BaseTransactionGroupInfo transactionGroupInfo = TransactionGroupInfo.getCurrent();
        if(transactionGroupInfo!=null) {
            httpRequest.getHeaders().add("groupInfo", transactionGroupInfo.toString());
        }
        ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest,bytes);
        List<String> groupInfoStringList=response.getHeaders().get("groupInfo");
        if(groupInfoStringList != null){
            BaseTransactionGroupInfo responseTransactionGroupInfo = TransactionGroupInfoFactory.getInstanceParseString(groupInfoStringList.get(0));
            transactionGroupInfo.addMemebers(responseTransactionGroupInfo.getGroupMembers());
        }
        return response;
    }
}
