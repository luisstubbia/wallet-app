package org.luisstubbia.walletapp.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncQueueConfig {

    @Value("${queue.core.pool.size}")
    private int corePoolSize;

    @Value("${queue.max.pool.size}")
    private int maxPoolSize;

    @Value("${queue.thread.prefix}")
    private String threadPrefix;


    @Bean(name="AsyncExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setThreadNamePrefix(threadPrefix);
        executor.initialize();
        return executor;
    }
}
