package com.luxf.thread.concurrent;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

/**
 * 用于多个工作线程之间交换数据的封装工具类。
 * 简单说就是一个线程在完成一定的事务后想与另一个线程交换数据，
 * 则第一个先拿出数据的线程会一直等待第二个线程，直到第二个线程拿着数据到来时才能彼此交换对应数据。
 * @author 小66
 */
public class ExchangerDemo {
    static class Producer extends Thread {
        private final Exchanger<Integer> exchanger;
        private static int data = 0;

        Producer(Exchanger<Integer> exchanger) {
            super("Producer-");
            this.exchanger = exchanger;
        }

        @Override
        public void run() {
            for (int i = 1; i < 5; i++) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    data = i;
                    System.out.println(getName() + " 交换前:" + data);
                    data = exchanger.exchange(data);
                    System.out.println(getName() + " 交换后:" + data);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Consumer extends Thread {
        private final Exchanger<Integer> exchanger;
        private static int data = 0;

        Consumer(Exchanger<Integer> exchanger) {
            super("Consumer-");
            this.exchanger = exchanger;
        }

        @Override
        public void run() {
            while (true) {
                System.out.println(getName() + " 交换前:" + data);
                try {
                    TimeUnit.SECONDS.sleep(1);
                    data = exchanger.exchange(data);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(getName() + " 交换后:" + data);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Exchanger<Integer> exchanger = new Exchanger<>();
        new Producer(exchanger).start();
        new Consumer(exchanger).start();
        TimeUnit.SECONDS.sleep(7);
        System.exit(-1);
    }
}