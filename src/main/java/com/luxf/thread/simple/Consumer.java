package com.luxf.thread.simple;

import lombok.SneakyThrows;

import java.util.Random;

/**
 * @author 小66
 * @date 2020-07-01 10:46
 **/
public class Consumer extends Thread {
    private static final Random RANDOM = new Random();
    private final Computer.ShareData shareData;

    Consumer(Computer.ShareData shareData) {
        this.shareData = shareData;
    }

    /**
     * 使用synchronized()块、输出的顺序不会乱, 一次生产, 一次消费！
     * 使用synchronized方法时, 输出的顺序会乱！
     */
    @SneakyThrows
    @Override
    public void run() {
        int number;
        do {
            // getNumber()方法没加锁, 此处不使用synchronized()块时, 就会抛出异常！
            // 因为getNumber()方法中存在wait()、notifyAll()方法、

            // 这里使用this对象就会报错、IllegalMonitorStateException, 因为是ShareData的实例对象调用的wait()、notifyAll(),不是当前对象！
            // synchronized(this){
            // 使用Computer.ShareData.class也会报同样的错、因为不是实例对象！
            // synchronized(Computer.ShareData.class){  --> 这种锁方式：如果方法加了锁, 就会出现死锁！
            synchronized (shareData) {
                // 因为是shareData实例对象,调用的notifyAll()、
                number = shareData.getNumber();
                System.out.println("Consume computer number = " + number);
            }
        } while (number < RANDOM.nextInt(2000));
    }
}
