package com.coconason.dtf.client.core.threadpools;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
/**
 * @Author: Jason
 * @date: 2018/8/22-17:22
 */
@Component
@PropertySource("classpath:application.properties")
class ThreadPoolForClientConfiguration {
    @Value(value="${threadpool.corePoolSize}")
    private Integer corePoolSize;
    @Value(value="${threadpool.maximumPoolSize}")
    private Integer maximumPoolSize;
    @Value(value="${threadpool.keepAliveTime}")
    private Integer keepAliveTime;
    @Value(value="${threadpool.queueSize}")
    private Integer queueSize;

    public Integer getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public Integer getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(Integer maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public Integer getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(Integer keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public Integer getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(Integer queueSize) {
        this.queueSize = queueSize;
    }
}
