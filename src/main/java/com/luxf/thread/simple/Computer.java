package com.luxf.thread.simple;

/**
 * @author 小66
 * @date 2020-07-01 10:35
 **/
public class Computer {
    public static void main(String[] args) {
        ShareData shareData = new ShareData();
        new Producer(shareData).start();
        new Consumer(shareData).start();
    }

    static class ShareData {
        private volatile boolean canConsume = false;
        private int number = 0;

        /**
         * 使用notify(), notifyAll(),wait(), wait(long), wait(long, int)时,
         * 如果方法没有添加synchronized锁, 则就需要对调用该方法时, 使用synchronized()块 --> 锁ShareData的实例对象！
         * 否则抛出IllegalMonitorStateException异常、
         *
         * @return
         * @throws InterruptedException
         */
        int getNumber() throws InterruptedException {
            while (!canConsume) {
                wait();
            }
            notifyAll();
            canConsume = false;
            return number;
        }

        void setNumber(int number) throws InterruptedException {
            while (canConsume) {
                wait();
            }
            this.number = number;
            notifyAll();
            canConsume = true;
        }
    }

}
