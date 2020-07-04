package com.luxf.thread.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

/**
 * ForkJoinTask：主要是用来将任务拆分, 执行完毕后、再将任务合并！
 * 比如将数据导出Excel时, 要按原始顺序进行导出时, 需要每个线程插入自己分配到的顺序范围内的数据。不会导致顺序错乱！
 * {@link RecursiveAction} 可以用于排序算法、
 * @author 小66
 * @date 2020-07-03 19:55
 **/
public class ForkJoinTaskDemo {
    private static final int SIZE = 6031;

    public static void main(String[] args) throws Exception {
        long singleStartTime = System.currentTimeMillis();
        /**
         *  估计是由于list.add()不耗时的缘故, 单线程比ForkJoinTask更快！
         */
        List<String> list = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            list.add(String.valueOf(i));
        }
        System.out.println("singleList = " + list.size());
        long singleEndTime = System.currentTimeMillis();
        System.out.println("singleEndTime-singleStartTime = " + (singleEndTime - singleStartTime));
        long multiStartTime = System.currentTimeMillis();
        ForkJoinTask<List<String>> forkJoinTask = new SubTask<>(new ArrayList<>(), 1, SIZE);
        ForkJoinPool joinPool = ForkJoinPool.commonPool();
        /**
         *  默认的poolParallelism == 7、
         */
        int poolParallelism = ForkJoinPool.getCommonPoolParallelism();
        System.out.println("poolParallelism = " + poolParallelism);
        ForkJoinTask<List<String>> submit = joinPool.submit(forkJoinTask);
        List<String> result = submit.get();
        long multiEndTime = System.currentTimeMillis();
        System.out.println("multiList = " + result.size());
        System.out.println("multiEndTime-multiStartTime = " + (multiEndTime - multiStartTime));
    }

    /**
     * 任务类必须继承自{@link RecursiveTask}(递归、有结果)或{@link RecursiveAction}(递归、无结果)
     * Recursive：递归、
     * @param <V>
     */
    static class SubTask<V> extends RecursiveTask<List<String>> {
        private static final int THRESHOLD = 500;
        private List<String> list;
        private int start;
        private int end;

        private SubTask(List<String> list, int start, int end) {
            this.start = start;
            this.end = end;
            this.list = list;
        }

        @Override
        protected List<String> compute() {
            // 判断一个任务是否足够小： 如果数据量不大, 就不需要拆分、
            if (end - start <= THRESHOLD && end - start > 0) {
                for (int i = start; i <= end; i++) {
                    list.add(String.valueOf(i));
                }
                return list;
            }
//            // TODO: 错误的示范-> 这种写法 类似于多线程直接执行任务、
//            int count = end / THRESHOLD;
//            ArrayList<SubTask<List<String>>> subTasks = new ArrayList<>();
//            for (int i = 0; i < count; i++) {
//                int startIndex = i == 0 ? start : i * THRESHOLD + 1;
//                int endIndex = (i + 1) * THRESHOLD > this.end ? this.end : (i + 1) * THRESHOLD;
//                SubTask<List<String>> subTask = new SubTask<>(new ArrayList<>(), startIndex, endIndex);
//                subTasks.add(subTask);
//            }
//            invokeAll(subTasks);
//            List<String> result = new ArrayList<>();
//            subTasks.forEach(task -> result.addAll(task.join()));

            // 任务太大, '分裂'子任务: --> TODO: 相当于实现递归、
            int middle = (end + start) / 2;
            System.out.println(String.format("split %d~%d ==> %d~%d, %d~%d", start, end, start, middle, middle, end));
            SubTask<List<String>> subTaskOne = new SubTask<>(new ArrayList<>(), start, middle);
            SubTask<List<String>> subTaskTow = new SubTask<>(new ArrayList<>(), middle + 1, end);
            // invokeAll()：并行运行子任务:
            invokeAll(subTaskOne, subTaskTow);
            // 获得子任务的结果:
            List<String> resultOne = subTaskOne.join();
            List<String> resultTow = subTaskTow.join();
            // 汇总结果:
            List<String> result = new ArrayList<>();
            result.addAll(resultOne);
            result.addAll(resultTow);
            return result;
        }
    }

}
