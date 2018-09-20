package com.coconason.dtf.client.core.nettyclient.protobufclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @Author: Jason
 * @date: 2018/8/22-17:22
 */
@Component
@PropertySource("classpath:application.properties")
class NettyServerConfiguration {
    @Value(value="${nettyserver.host}")
    private String host;
    @Value(value="${nettyserver.port}")
    private Integer port;

    String getHost() {
        return host;
    }

    void setHost(String host) {
        this.host = host;
    }

    Integer getPort() {
        return port;
    }

    void setPort(Integer port) {
        this.port = port;
    }
}
