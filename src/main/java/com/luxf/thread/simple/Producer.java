package com.luxf.thread.simple;

import lombok.SneakyThrows;

/**
 * @author 小66
 * @date 2020-07-01 10:42
 **/
public class Producer extends Thread {

    private final Computer.ShareData shareData;

    Producer(Computer.ShareData shareData) {
        this.shareData = shareData;
    }

    @SneakyThrows
    @Override
    public void run() {
        int i = 0;
        while (true) {
            synchronized (shareData) {
                i++;
                shareData.setNumber(i);
                System.out.println("Product computer number = " + i);
            }
        }
    }
}
