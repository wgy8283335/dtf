package com.coconason.dtf.client.core.spring.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import java.io.IOException;

/**
 * @Author: Jason
 * @date: 2018/8/10-10:20
 */
public class DtfHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private Logger logger = LoggerFactory.getLogger(DtfHttpRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        TxTransactionLocal txTransactionLocal = TxTransactionLocal.current();
        String groupId = txTransactionLocal == null ? null : txTransactionLocal.getGroupId();

        logger.debug("DTF-SpringCloud TxGroup info -> groupId:"+groupId);

        if(txTransactionLocal!=null) {
            httpRequest.getHeaders().add("tx-group", groupId);
        }
        return clientHttpRequestExecution.execute(httpRequest,bytes);
    }
}
