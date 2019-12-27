package com.martyn.message.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PoolConfig {

    @Value("${pool.core_size}")
    private int coolPoolSize;

    @Value("${pool.max_size}")
    private int maxPoolSize;

    @Value("${pool.keepalive_second}")
    private int keepAliveSecond;

    @Value("${pool.queue_capacity}")
    private int queueCapacity;

    public int getCoolPoolSize() {
        return coolPoolSize;
    }

    public void setCoolPoolSize(int coolPoolSize) {
        this.coolPoolSize = coolPoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getKeepAliveSecond() {
        return keepAliveSecond;
    }

    public void setKeepAliveSecond(int keepAliveSecond) {
        this.keepAliveSecond = keepAliveSecond;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }
}