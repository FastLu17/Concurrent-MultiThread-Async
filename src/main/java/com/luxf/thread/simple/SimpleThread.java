package com.luxf.thread.simple;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author 小66
 * @date 2020-07-01 8:37
 **/
public class SimpleThread {
    private static final Random RANDOM = new Random();

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            System.out.println("RANDOM.nextInt(20) = " + RANDOM.nextInt(20));
            Thread currentThread = Thread.currentThread();
//            如果此处 join()、则主线程中输出的 STATE: WAITING
            try {
                currentThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Thread.State currentThreadState = currentThread.getState();
            boolean currentThreadAlive = currentThread.isAlive();
            System.out.println("currentThreadAlive = " + currentThreadAlive);
            System.out.println("currentThreadState = " + currentThreadState);
        });
        // 线程的状态
        Thread.State state = thread.getState();
        // 是否存活
        boolean alive = thread.isAlive();
        boolean daemon = thread.isDaemon();
        System.out.println("state = " + state);
        System.out.println("alive = " + alive);
        System.out.println("daemon = " + daemon);
        thread.start();

        // 如果没有等待, 后续输出状态则是 --> State: RUNNABLE, Alive: true
        TimeUnit.SECONDS.sleep(3);

        Thread.State afterStartState = thread.getState();
        boolean afterStartAlive = thread.isAlive();
        boolean afterStartInterrupted = thread.isInterrupted();
        System.out.println("afterStartState = " + afterStartState);
        System.out.println("afterStartAlive = " + afterStartAlive);
        System.out.println("afterStartInterrupted = " + afterStartInterrupted);

    }
}
