package com.luxf.thread.queue;

import java.util.Random;
import java.util.concurrent.*;

/**
 * @author 小66
 * @date 2020-07-04 22:23
 **/
public class ConcurrentLinkedQueueDemo {

    private static final Random RANDOM = new Random();
    private static final int LENGTH = 30;

    public static void main(String[] args) throws InterruptedException {
        // testConcurrentLinkedQueue();
        testConcurrentLinkedDeque();

    }

    private static void testConcurrentLinkedQueue() throws InterruptedException {
        ConcurrentLinkedQueue<Integer> concurrentQueue = new ConcurrentLinkedQueue<>();
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
        for (int i = 0; i < LENGTH; i++) {
            fixedThreadPool.submit(() -> {
                boolean offer = concurrentQueue.offer(RANDOM.nextInt(LENGTH));
                System.out.println("offer = " + offer);
            });
        }
        TimeUnit.SECONDS.sleep(2);
        System.out.println("concurrentQueue = " + concurrentQueue);
        for (int i = 0; i < LENGTH; i++) {
            // 模拟并发消费队列、
            fixedThreadPool.execute(() -> {
                Integer poll = concurrentQueue.poll();
                System.out.println("poll = " + poll);
            });
        }
        fixedThreadPool.shutdown();
    }

    private static void testConcurrentLinkedDeque() throws InterruptedException {
        ConcurrentLinkedDeque<Integer> concurrentDeque = new ConcurrentLinkedDeque<>();
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
        for (int i = 0; i < LENGTH; i++) {
            fixedThreadPool.submit(() -> {
                boolean offer = concurrentDeque.offer(RANDOM.nextInt(LENGTH));
                System.out.println("offer = " + offer);
            });
        }
        TimeUnit.SECONDS.sleep(2);
        System.out.println("concurrentDeque = " + concurrentDeque);
        for (int i = 0; i < LENGTH; i++) {
            // 模拟并发消费队列、
            int finalI = i;
            fixedThreadPool.execute(() -> {
                Integer poll;
                if (finalI % 2 == 0) {
                    poll = concurrentDeque.poll();
                } else {
                    poll = concurrentDeque.pollLast();
                }
                System.out.println("poll = " + poll);
            });
        }
        fixedThreadPool.shutdown();
    }
}
