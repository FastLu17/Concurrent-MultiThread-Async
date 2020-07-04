package com.luxf.thread.concurrent;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 信号量对象：可以限制多线程并发数量！常用于限制可以访问某些资源的线程数量、
 * 当没有可用 的许可证(availablePermits==0)时,
 *
 * @author 小66
 * @date 2020-07-01 16:04
 **/
public class SemaphoreDemo {
    /**
     * permits：许可证数量、最多并发的数量
     */
    private static Semaphore semaphore = new Semaphore(3, true);

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            final long num = i;
            executorService.submit(() -> {
                try {
                    /**
                     * tryAcquire：非阻塞方法、
                     * acquire：阻塞方法、会一直等待！
                     */
                    //获取许可、
                    boolean tryAcquire = semaphore.tryAcquire(2, TimeUnit.SECONDS);
                    if (tryAcquire) {
                        int queueLength = semaphore.getQueueLength();
                        System.out.println("queueLength = " + queueLength);
                        //执行
                        System.out.println("Accessing: " + num);
                        TimeUnit.SECONDS.sleep(new Random().nextInt(8)); // 模拟随机执行时长
                        //释放
                        semaphore.release();
                        System.out.println("Release..." + num);
                    } else {
                        System.out.println("没有得到许可：num = " + num);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown();
    }
}
