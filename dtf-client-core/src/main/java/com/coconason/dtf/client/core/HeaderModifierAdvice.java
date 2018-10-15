package com.coconason.dtf.client.core;

import com.coconason.dtf.client.core.beans.BaseTransactionGroupInfo;
import com.coconason.dtf.client.core.beans.TransactionGroupInfo;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletResponse;

/**
 * @Author: Jason
 * @date: 2018/9/23-10:20
 */
@ControllerAdvice
public final class HeaderModifierAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
                                  ServerHttpResponse response) {
        ServletServerHttpResponse ssResp = (ServletServerHttpResponse)response;
        HttpServletResponse resp = ssResp.getServletResponse();
        BaseTransactionGroupInfo groupInfo = TransactionGroupInfo.getCurrent();
        if(groupInfo != null){
            resp.setHeader("groupInfo",groupInfo.toString());
        }
        return body;
    }
}
