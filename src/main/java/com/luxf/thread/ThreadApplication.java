package com.luxf.thread;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author 小66
 * @date 2020-07-03 22:08
 **/
@SpringBootApplication
@EnableAsync
public class ThreadApplication {
    public static void main(String[] args) {
        SpringApplication.run(ThreadApplication.class);
    }
}
