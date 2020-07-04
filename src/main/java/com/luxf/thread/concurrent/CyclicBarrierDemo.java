package com.luxf.thread.concurrent;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * 使用场景：让多线程任务 '几乎同时' 进行执行。
 *
 * @author 小66
 * @date 2020-07-01 14:46
 **/
public class CyclicBarrierDemo {

    public static void main(String[] args) throws InterruptedException {
        int threadNum = 5;
        /**
         * 有2个构造方法、public CyclicBarrier(int parties, Runnable barrierAction);
         * parties：参与任务的线程个数、
         * barrierAction：最后一个到达要做的任务(调用{@link CyclicBarrier#await()}方法->表示到达任务开始出！)的线程
         */
        CyclicBarrier barrier = new CyclicBarrier(threadNum, () -> System.out.println(Thread.currentThread().getName() + " 完成最后任务"));

        for (int i = 0; i < threadNum; i++) {
            new TaskThread(barrier).start();
        }
        TimeUnit.SECONDS.sleep(7L);
        boolean broken = barrier.isBroken();
        System.out.println("broken = " + broken);

        boolean barrierBroken = barrier.isBroken();
        System.out.println("barrierBroken = " + barrierBroken);
    }

    static class TaskThread extends Thread {

        CyclicBarrier barrier;

        TaskThread(CyclicBarrier barrier) {
            this.barrier = barrier;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                System.out.println(getName() + " 到达栅栏 A");
                /**
                 *  await()：等到所有{Parties参与方}都在此屏障上调用await()。
                 *  线程调用await()表示自己已经到达栅栏。
                 */
                barrier.await();
                System.out.println(getName() + " 冲破栅栏 A");
                // 此处可以不执行reset()、
                barrier.reset();
                Thread.sleep(2000);
                System.out.println(getName() + " 到达栅栏 B");
                barrier.await();
                System.out.println(getName() + " 冲破栅栏 B");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
