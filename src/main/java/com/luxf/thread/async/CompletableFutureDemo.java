package com.luxf.thread.async;

import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.ListenableFutureTask;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 获取{@link CompletableFuture}对象时,传递了Executor线程池对象,则需要手动结束线程池、
 * <p>
 * TODO: 如果获取{@link CompletableFuture}对象时, 没有指定线程池, 默认使用的线程池会立刻关闭CompletableFuture。
 * <p>
 * TODO: CompletableFuture相比Future的特别之处：
 * 1、实现了异步回调机制。
 * 2、多个CompletableFuture可以串行执行：thenApply()等方法(thenXXX)。
 * <p>
 * {@link ListenableFuture}和{@link CompletableFuture} 都是对异步任务的结果获取回调、
 *
 * @author 小66
 * @date 2020-07-03 18:40
 **/
public class CompletableFutureDemo {
    private static Random rand = new Random();

    private static int getMoreData() {
        sleep();
        return rand.nextInt(1000);
    }

    public static void main(String[] args) {
        testListenableFutureTask();
        /*
         * TODO: Future对象是顶级父类、会出现ClassCastException
         * */
//        Future<String> stringFuture = Executors.newCachedThreadPool().submit(() -> {
//            TimeUnit.SECONDS.sleep(3);
//            return "AAA";
//        });
//        CompletableFuture<String> completableFuture = (CompletableFuture<String>) stringFuture; // ClassCastException

        /**
         *  TODO: 这样使用时, 整个main线程不会结束、为什么？--->线程池未关闭、Executor 需要手动结束线程池, 否则造成线程泄露！
         */
        ExecutorService pool = Executors.newCachedThreadPool();

        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(CompletableFutureDemo::getMoreData, pool);

        // 异步操作、
        future.whenCompleteAsync((integer, throwable) -> {
            if (throwable == null) {
                System.out.println("value in whenCompleteAsync() = " + integer + " on " + System.currentTimeMillis());
            } else {
                throw new RuntimeException(throwable);
            }
        });


        System.out.println("main....");

        pool.shutdown();

        System.out.println("pool.isShutdown() = " + pool.isShutdown());

        new CompletableFutureDemo().thenAcceptBoth();

        sleep(10);

    }

    /**
     * 使用{@link ListenableFutureTask} 和 {@link CompletableFuture} 的不同
     */
    private static void testListenableFutureTask() {
        // ListenableFutureTask extends FutureTask implements RunnableFuture extends Runnable
        ListenableFutureTask<String> listenableFutureTask = new ListenableFutureTask<>(() -> {
            TimeUnit.SECONDS.sleep(2);
            return "ABC";
        });
//        CompletableFuture<String> completable = listenableFutureTask.completable();
//        completable.whenCompleteAsync((s, throwable) -> System.out.println("s = " + s));
        listenableFutureTask.addCallback(new ListenableFutureCallback<String>() {
            @Override
            public void onFailure(Throwable ex) {
                System.out.println("ex = " + ex);
            }

            @Override
            public void onSuccess(String result) {
                System.out.println("result = " + result);
            }
        });
        // 阻塞的
        // listenableFutureTask.run();

        // ListenableFutureTask需要使用线程来启动任务！
        new Thread(listenableFutureTask).start();
    }

    /**
     * 监听当前CompletableFuture对象是否计算完成、(监听异步任务是否完成)
     * 可以处理异常、
     * TODO: exceptionally() 方法可以单独监听异常！
     */

    public void whenComplete() {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            int nextInt = rand.nextInt(5);
            if (nextInt % 2 == 0) {
                int i = 12 / 0;
            }
            return nextInt;
        });

        future.whenComplete((integer, throwable) -> {
            if (throwable == null) {
                System.out.println("v = " + integer);
            } else {
                System.out.println("throwable = " + throwable);
                throw new RuntimeException(throwable);
            }
        });

        // 监听异常：出现异常
        future.exceptionally(t -> {
            System.out.println("执行失败！" + t.getMessage());
            // 返回值类型是 Void、
            return null;
        });

        // 如果没有指定线程池, 默认使用的线程池会立刻关闭CompletableFuture、
        sleep();
    }

    /**
     * 当一个线程依赖另一个线程时，可以使用 thenApply 方法来把这两个线程串行化。
     * <p>
     * thenApply()依赖上一个CompletableFuture的返回值、
     */
    public void thenApply() throws Exception {
        CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> {
            long result = new Random().nextInt(100);
            System.out.println("result1=" + result);
            return result;
        }).thenApply(t -> {
            long result = t * 5;
            System.out.println("result2=" + result);
            return result;
        });

        long result = future.get();
        System.out.println(result);

        // 如果没有指定线程池, 默认使用的线程池会立刻关闭CompletableFuture、
        sleep();
    }

    /**
     * handle()方法：接收上一个CompletableFuture的返回值, 并且可以处理上一个CompletableFuture的异常、
     * <p>
     * handle：是在任务完成后再执行，可以处理异常的任务。
     * thenApply：只可以执行正常的任务，无法处理上一个任务的异常。
     */

    public void handle() throws Exception {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            int i = 10 / 0;
            return i;
        }).handle((param, throwable) -> {
            int result;
            if (throwable == null) {
                result = param * 2;
            } else {
                result = 0;
                System.out.println(throwable.getMessage());
            }
            return result;
        });
        System.out.println(future.get());

        // 如果没有指定线程池, 默认使用的线程池会立刻关闭CompletableFuture、
        sleep();
    }

    /**
     * 接收上一个CompletableFuture返回值(计算结果)，并消费处理，无返回结果。
     *
     * @throws Exception
     */

    public void thenAccept() throws Exception {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> rand.nextInt(10))
                .thenAccept(integer -> {
                    System.out.println("integer = " + integer);
                    System.out.println("integer*3 = " + integer * 3);
                });

        Void aVoid = future.get();
        System.out.println("aVoid = " + aVoid);

        // 如果没有指定线程池, 默认使用的线程池会立刻关闭CompletableFuture、
        sleep();
    }


    /**
     * thenRun()：不关心计算结果,只要上一个CompletableFuture执行完成, 就开始执行thenAccept。
     * <p>
     * thenRun()：会在上一个CompletableFuture异步任务执行完成后, 才开始执行、
     */
    public void thenRun() {
        CompletableFuture<Integer> supplyAsync = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(5000);
                System.out.println("supplyAsync ...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return new Random().nextInt(10);
        });


        supplyAsync.thenRun(() -> {
            try {
                System.out.println("supplyAsync.get() = " + supplyAsync.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("thenRun ...");
        });

        // 如果没有指定线程池, 默认使用的线程池会立刻关闭CompletableFuture、
        sleep();
    }

    /**
     * thenCombine(合并任务): 会把 两个 CompletionStage 的任务都执行完成后，
     * 把两个任务的结果一块交给 thenCombine() 来处理。 TODO: 有返回值, 可以进行后续操作！
     */
    public void thenCombine() {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "Future");

        future1.thenCombine(future2, (t, u) -> t + "-" + u)
                .thenAccept(s -> System.out.println("s = " + s));
        // 如果没有指定线程池, 默认使用的线程池会立刻关闭CompletableFuture、
        sleep();
    }

    /**
     * thenAcceptBoth：同时消耗两个资源(返回结果)、TODO: 无返回值！
     * <p>
     * 当两个CompletionStage都执行完成后，把结果一块交给thenAcceptBoth来进行消耗
     */
    public void thenAcceptBoth() {
        CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(() -> {
            int t = rand.nextInt(3);
            sleep(t);
            System.out.println("f1=" + t);
            return t;
        });

        CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(() -> {
            int t = rand.nextInt(3);
            sleep(t);
            System.out.println("f2=" + t);
            return t;
        });

        // allOf()：所有的 CompletableFuture 都执行完成、
        CompletableFuture<Void> allOfFuture = CompletableFuture.allOf(f1, f2);
        allOfFuture.thenAccept(aVoid -> System.out.println("aVoid = " + aVoid));

        // anyOf()：任意一个 CompletableFuture 执行完成、
        CompletableFuture<Object> anyOfFuture = CompletableFuture.anyOf(f1, f2);
        anyOfFuture.thenAccept(obj -> System.out.println("obj = " + obj));

        f1.thenAcceptBoth(f2, (t, u) -> System.out.println("f1=" + t + "; f2=" + u + ";"));

        // 如果没有指定线程池, 默认使用的线程池会立刻关闭CompletableFuture、
        sleep();
    }


    private static void sleep() {
        sleep(2);
    }

    private static void sleep(int time) {
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
