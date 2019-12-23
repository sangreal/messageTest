package com.martyn.message.common;


import com.martyn.message.config.PoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class ThreadHelper {
    @Autowired
    private PoolConfig poolConfig;

    public ThreadPoolExecutor poolExecutor;

    @PostConstruct
    private void init() {
        poolExecutor = new ThreadPoolExecutor(poolConfig.getCoolPoolSize(),
                poolConfig.getMaxPoolSize(),
                poolConfig.getKeepAliveSecond(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(poolConfig.getQueueCapacity()));
    }

    public ExecutorService getExecutor() {
        return poolExecutor;
    }
}
