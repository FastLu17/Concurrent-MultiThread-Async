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
     * TODO: 注意、permits可以为0和负数！ 如果为0时, 则需要先 release(), 再进行acquire();  直接执行acquire(),则会一直等待, 直到 availablePermits > 0 !
     *      为负数时, 则release()一次、availablePermits +1、
     *      一般不会直接初始化为负数, 但是在特殊情况下(2个线程有顺序要求的情况！), 会初始化为0、
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

    /**
     * 依次输出 foobar foobar
     */
    public class IntervalPrint {

        private final Semaphore fooSemaphore = new Semaphore(1);
        /**
         * TODO: 初始化为0、确保 barSemaphore 的线程在 fooSemaphore 线程释放了barSemaphore的信号之后才能获取到、 确保了对应的顺序
         */
        private final Semaphore barSemaphore = new Semaphore(0);
        private int a = 4;

        public void main() {
            ExecutorService threadPool = Executors.newCachedThreadPool();
            for (int i = 0; i < a; i++) {
                threadPool.execute(() -> {
                    try {
                        fooSemaphore.acquire();
                        printFoo();
                        int permits = barSemaphore.availablePermits();
                        System.out.println("permits = " + permits);
                        barSemaphore.release();
                        int availablePermits = barSemaphore.availablePermits();
                        System.out.println("availablePermits = " + availablePermits);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
            }

            for (int i = 0; i < a; i++) {
                threadPool.execute(() -> {
                    try {
                        // 由于初始化permits=0, 在没有执行barSemaphore.release()之前, 一直处于等待状态！
                        barSemaphore.acquire();
                        printBar();
                        fooSemaphore.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
            }
            threadPool.shutdown();
        }

        private void printBar() {
            System.out.print("bar ");
        }

        private void printFoo() {
            System.out.print("foo");
        }
    }

}
