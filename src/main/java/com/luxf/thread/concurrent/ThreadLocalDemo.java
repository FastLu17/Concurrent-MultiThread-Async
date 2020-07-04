package com.luxf.thread.concurrent;

import java.util.concurrent.TimeUnit;

/**
 * @author 小66
 * @date 2020-07-01 12:31
 **/
public class ThreadLocalDemo {
    /**
     * ThreadLocal：子线程不可共享父线程的局部变量、
     */
    private static final ThreadLocal<Integer> INTEGER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * InheritableThreadLocal：子线程可以共享父线程的局部变量, 无法修改主线程的局部变量的的值！
     */
    private static final ThreadLocal<String> STRING_THREAD_LOCAL = new InheritableThreadLocal<>();

    public static void main(String[] args) throws InterruptedException {
        INTEGER_THREAD_LOCAL.set(20);
        STRING_THREAD_LOCAL.set("INIT");
        new Thread(() -> {
            Integer integer = INTEGER_THREAD_LOCAL.get();
            System.out.println("integer = " + integer);
            String string = STRING_THREAD_LOCAL.get();
            System.out.println("string = " + string);
            STRING_THREAD_LOCAL.set("CHILD THREAD CHANGE VALUE");
            System.out.println("STRING_THREAD_LOCAL.get() = " + STRING_THREAD_LOCAL.get());
            STRING_THREAD_LOCAL.remove();
        }).start();

        TimeUnit.SECONDS.sleep(2);
        String mainString = STRING_THREAD_LOCAL.get();
        Integer mainInteger = INTEGER_THREAD_LOCAL.get();
        System.out.println("mainInteger = " + mainInteger);
        System.out.println("mainString = " + mainString);
        INTEGER_THREAD_LOCAL.remove();
        STRING_THREAD_LOCAL.remove();
    }
}
