package com.dtf.client.core.spring.client;

import com.dtf.client.core.beans.group.BaseTransactionGroupInfo;
import com.dtf.client.core.beans.group.TransactionGroupInfo;
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
 * Controller Advice used to modify response header.
 * 
 * @author wangguangyuan
 */
@ControllerAdvice
public final class HeaderModifierAdvice implements ResponseBodyAdvice<Object> {
    
    /**
     * Intercept all of controller.
     * 
     * @param returnType method parameter
     * @param converterType http message converter
     * @return true
     */
    @Override
    public boolean supports(final MethodParameter returnType, final Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }
    
    /**
     * Set group information in the response header.
     * 
     * @param body response body
     * @param returnType return type
     * @param selectedContentType media type
     * @param selectedConverterType http message converter
     * @param request server http request
     * @param response response
     * @return return response body
     */
    @Override
    public Object beforeBodyWrite(final Object body, final MethodParameter returnType, final MediaType selectedContentType,
                                  final Class<? extends HttpMessageConverter<?>> selectedConverterType, final ServerHttpRequest request,
                                  final ServerHttpResponse response) {
        ServletServerHttpResponse ssResp = (ServletServerHttpResponse) response;
        HttpServletResponse resp = ssResp.getServletResponse();
        BaseTransactionGroupInfo groupInfo = TransactionGroupInfo.getCurrent();
        if (groupInfo != null) {
            resp.setHeader("groupInfo", groupInfo.toString());
        }
        return body;
    }
    
}
