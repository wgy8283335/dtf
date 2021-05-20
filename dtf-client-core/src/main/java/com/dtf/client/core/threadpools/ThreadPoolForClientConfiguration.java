package com.dtf.client.core.threadpools;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Thread pool for client configuration.
 * 
 * @Author: wangguangyuan
 */
@Component
@PropertySource("classpath:application.properties")
public final class ThreadPoolForClientConfiguration {
    
    /**
     * Core size of pool.
     */
    @Value(value = "${threadpool.corePoolSize}")
    private Integer corePoolSize;
    
    /**
     * maximum size of pool.
     */
    @Value(value = "${threadpool.maximumPoolSize}")
    private Integer maximumPoolSize;
    
    /**
     * Keep alive time.
     */
    @Value(value = "${threadpool.keepAliveTime}")
    private Integer keepAliveTime;
    
    /**
     * Queue size.
     */
    @Value(value = "${threadpool.queueSize}")
    private Integer queueSize;
    
    /**
     * Get core size of pool.
     * 
     * @return
     */
    public Integer getCorePoolSize() {
        return corePoolSize;
    }
    
    /**
     * Get maximum size of pool.
     * 
     * @return
     */
    public Integer getMaximumPoolSize() {
        return maximumPoolSize;
    }
    
    /**
     * Get keep alive time.
     * 
     * @return
     */
    public Integer getKeepAliveTime() {
        return keepAliveTime;
    }

    /**
     * Get queue size.
     * 
     * @return
     */
    public Integer getQueueSize() {
        return queueSize;
    }
    
}
