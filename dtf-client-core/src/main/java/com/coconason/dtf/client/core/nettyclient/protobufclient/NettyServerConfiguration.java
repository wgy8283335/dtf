package com.coconason.dtf.client.core.nettyclient.protobufclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Netty service configuration.
 * 
 * @Author: Jason
 */
@Component
@PropertySource("classpath:application.properties")
final class NettyServerConfiguration {

    @Value(value = "${nettyserver.host}")
    private String host;

    @Value(value = "${nettyserver.port}")
    private Integer port;

    String getHost() {
        return host;
    }

    Integer getPort() {
        return port;
    }

}
