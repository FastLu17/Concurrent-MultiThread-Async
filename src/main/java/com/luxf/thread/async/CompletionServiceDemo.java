package com.luxf.thread.async;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * TODO: {@link ExecutorCompletionService}内部多维护了一个{@link LinkedBlockingQueue}、 因此阻塞时间更短、
 * Future和CompletionService的区别：
 * 1、Future：结果的输出和线程的放入顺序有关(如果前面的没完成,就算后面的哪个完成了也得等到你的牌号才能输出！)、阻塞更加耗时
 * 2、CompletionService：因为内部实现了阻塞队列, 完成的任务就添加到队列中！在从阻塞队列中取出数据, 阻塞耗时更短！
 * <p>
 * TODO: 正常多线程功能开发中, 一般不会使用阻塞的形式,使用阻塞、 更多的是使用{@link CompletableFuture}完成非阻塞的操作、或者是实现{@link FutureTask#done()}方法--> 任务结束后要干啥！
 *
 * @author 小66
 * @date 2020-07-04 16:59
 **/
public class CompletionServiceDemo {
    public static void main(String[] args) throws Exception {
        testFuture();
        testCompletionService();
    }

    /**
     * 结果的输出和线程的放入顺序有关(如果前面的没完成，就算后面的哪个完成了也得等到你的牌号才能输出！)、阻塞更加耗时
     * 因为是将任务直接添加到List中, 对List进行for循环时、前面的任务没完成, 后面的任务已完成！ 也不能出去后面已完成的任务先输出！
     */
    private static void testFuture() throws InterruptedException, ExecutionException {
        long startTime = System.currentTimeMillis();
        System.out.println("testFuture()开始执行：" + startTime);
        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<String>> result = new ArrayList<>();
//        BlockingQueue<Future<String>> blockingQueue = new ArrayBlockingQueue<>(20);
        for (int i = 5; i > 0; i--) {
            Future<String> submit = executor.submit(new Task(i));
            /**
             * TODO: 被注释掉的部分 -> 利用BlockingQueue和CompletableFuture完成类似的CompletionService的具体实现！
             */
//            CompletableFuture.supplyAsync(() -> {
//                try {
//                    submit.get();
//                    return submit;
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }).whenCompleteAsync((stringFuture, throwable) -> {
//                if (throwable == null) {
//                    blockingQueue.add(stringFuture);
//                }
//            });
            result.add(submit);
        }
        executor.shutdown();
        for (int i = 0; i < 5; i++) {
            Thread.sleep(500);
            System.out.println("线程" + i + "执行完成:" + result.get(i).get());
//            System.out.println("线程" + i + "执行完成:" + blockingQueue.take().get());
        }
        System.out.println("testFuture()执行完成:" + System.currentTimeMillis() + "," + (System.currentTimeMillis() - startTime));
    }

    /**
     * 结果的输出和线程的放入顺序无关(谁完成了谁就先输出！主线程总是能够拿到最先完成的任务的返回值，而不管它们加入线程池的顺序)。阻塞时间稍晚较短！
     * {@link CompletionService}TODO: tack()/poll()操作：获取并移除(从队列中移除)下一个完成的任务！谁先完成, 就获取谁！阻塞时间稍晚较短！
     * CompletionService可以进行循环的tack()/poll()操作！相当于Iterator的next()、
     * 因为实现类的内部维护了一个阻塞队列{@link ExecutorCompletionService#completionQueue}
     */
    private static void testCompletionService() throws InterruptedException, ExecutionException {
        long startTime = System.currentTimeMillis();
        System.out.println("testCompletionService()开始执行：" + startTime);
        ExecutorService executor = Executors.newCachedThreadPool();
        CompletionService<String> completionService = new ExecutorCompletionService<>(executor);
        for (int i = 5; i > 0; i--) {
            // 提交任务
            completionService.submit(new Task(i));
        }
        executor.shutdown();
        for (int i = 0; i < 5; i++) {
            // 检索并移除表示下一个已完成任务的 Future, 如果目前不存在这样的任务，则等待。take()会阻塞、poll()直接返回！
            Future<String> future = completionService.take();
            Thread.sleep(500);
            System.out.println("线程" + i + "执行完成:" + future.get());   // 这一行在这里不会阻塞，引入放入队列中的都是已经完成的任务
        }
        System.out.println("testCompletionService()执行完成:" + System.currentTimeMillis() + "," + (System.currentTimeMillis() - startTime));
    }

    private static class Task implements Callable<String> {

        private volatile int i;

        Task(int i) {
            this.i = i;
        }

        @Override
        public String call() throws Exception {
            Thread.sleep(i * 500);
            return "任务 : " + i;
        }

    }
}
