package com.luxf.thread.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 小66
 * @date 2020-07-03 22:22
 **/
@Configuration
public class AsyncTaskPoolConfig {

    @Bean("asyncTaskExecutor")
    public Executor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 线程池的基本大小，即在没有任务需要执行的时候线程池的大小，并且只有在工作队列满了的情况下才会创建超出这个数量的线程。
        executor.setCorePoolSize(10);
        // 线程池中允许的最大线程数，线程池中的当前线程数目不会超过该值、
        executor.setMaxPoolSize(20);
        // 设置队列容量、
        executor.setQueueCapacity(200);
        // 如果一个线程处在空闲状态的时间超过了该属性值，就会因为超时而退出。
        executor.setKeepAliveSeconds(60);
        // 控制是否允许核心线程超时退出
        executor.setAllowCoreThreadTimeOut(false);
        executor.setThreadNamePrefix("asyncTaskExecutor-");//设置线程的名称前缀
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
}
