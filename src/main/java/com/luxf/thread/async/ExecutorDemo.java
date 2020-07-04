package com.luxf.thread.async;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

/**
 * @author 小66
 * @date 2020-07-01 14:07
 **/
public class ExecutorDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
        Future<String> submit = fixedThreadPool.submit(() -> "ABC");
        String s = submit.get();
        System.out.println("s = " + s);
        /**
         * TODO: 非定时任务的线程池, 需要手动关闭线程池, 否则引起线程泄露！
         */
        fixedThreadPool.shutdown();

        // 定时任务的线程池, 如果关闭了, 就不会执行！
        scheduledThreadPool.scheduleWithFixedDelay(() -> System.out.println("Thread.currentThread().getName() = " + Thread.currentThread().getName()), 2, 4, TimeUnit.SECONDS);
    }

    /**
     * 手动创建线程池, 实际工作中, 更常见！
     *
     * @return
     */
    public Executor getThreadPoolTaskExecutor() {
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
        executor.setThreadNamePrefix("taskExecutor-");//设置线程的名称前缀
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
}
