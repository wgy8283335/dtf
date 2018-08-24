package com.coconason.dtf.client.core.spring.http;

import com.coconason.dtf.client.core.beans.TransactionGroupInfo;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: Jason
 * @date: 2018/8/23-16:02
 */

public class DtfHttpResponseInterceptor extends HandlerInterceptorAdapter{

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        TransactionGroupInfo groupInfo = TransactionGroupInfo.getCurrent();
        //set groupInfo in the head.
        response.setHeader("groupId",groupInfo.getGroupId());
        response.setHeader("groupMembers",groupInfo.getGroupMembers().toString());
    }
}
