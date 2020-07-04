package com.luxf.thread.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * {@link CountDownLatch} 是一个计数器，线程完成一个记录一个，计数器递减，只能只用一次！
 * {@link CyclicBarrier} 的计数器更像一个阀门，需要所有线程都到达，然后继续执行，计数器递增，提供reset功能，可以多次使用
 *
 * @author 小66
 * @date 2020-07-01 14:28
 **/
public class CountDownLatchDemo {
    /**
     * 执行一次countDown()方法, count数量 -1！
     * 如果 初始值的数量, 大于消费线程的数量！则 await()方法一直阻塞！
     * 计数器的初始值并不一定是线程的数量，完全可以一个线程countDown两次！
     */
    private static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(2);

    public static void main(String[] args) {
        System.out.println("主线程开始执行…… ……");
        //第一个子线程执行
        ExecutorService es1 = Executors.newSingleThreadExecutor();
        es1.execute(() -> {
            try {
                Thread.sleep(3000);
                System.out.println("子线程：" + Thread.currentThread().getName() + "执行");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            COUNT_DOWN_LATCH.countDown();
            System.out.println("COUNT_DOWN_LATCH--1 = " + COUNT_DOWN_LATCH.toString());
        });
        es1.shutdown();

        //第二个子线程执行
        ExecutorService es2 = Executors.newSingleThreadExecutor();
        es2.execute(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("子线程：" + Thread.currentThread().getName() + "执行");
            COUNT_DOWN_LATCH.countDown();
            System.out.println("COUNT_DOWN_LATCH--2 = " + COUNT_DOWN_LATCH.toString());
        });
        es2.shutdown();
        System.out.println("等待两个线程执行完毕…… ……");
        try {
            COUNT_DOWN_LATCH.await();
            // 如果等2秒后, count值(COUNT_DOWN_LATCH.getCount())还没变为 0 的话就会继续执行、
            // COUNT_DOWN_LATCH.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("两个子线程都执行完毕，继续执行主线程");
    }
}
