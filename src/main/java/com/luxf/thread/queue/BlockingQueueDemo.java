package com.luxf.thread.queue;

import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.*;

/**
 * TODO: Queue（单向队列）和 Deque（双向队列）
 * <p>
 * 并发阻塞队列和非阻塞队列：队列遵循先进先出，后进后出的原则。FIFO：先进先出
 * 1、{@link ConcurrentLinkedQueue} 为代表的高性能队列非阻塞、 ConcurrentLinkedQueue,ConcurrentLinkedDeque
 * 2、{@link BlockingQueue} 接口为代表的阻塞队列、阻塞队列是线程安全。
 * <p>
 * 入列(存)：存放的队列超出队列的容量, 就会阻塞。
 * 出列(取)：队列是空的, 就会阻塞、
 * <p>
 * 1.ArrayDeque （数组双端队列）
 * 2.PriorityQueue （优先级队列）
 * 3.ConcurrentLinkedQueue （基于链表的无界并发队列）：基于CAS实现, 适用于高并发场景下的队列, 该队列不允许null元素。
 * 4.DelayQueue （延期阻塞队列）
 * 5.ArrayBlockingQueue （基于数组的有界阻塞队列）
 * 6.LinkedBlockingQueue （基于链表的无界阻塞队列）：(默认capacity=Integer.MAX_VALUE)
 * 7.LinkedBlockingDeque （基于链表的双端无界阻塞队列）：(默认capacity=Integer.MAX_VALUE)
 * 8.PriorityBlockingQueue （带优先级的无界阻塞队列）：(默认capacity=11), 该队列不允许null元素。只保证第一个元素是按照{@link Comparator}的具体实现、入队元素 必须实现 Comparable 接口, 否则报错
 * 9.SynchronousQueue （并发同步阻塞队列）：仅允许容纳一个元素。当一个线程插入一个元素后会被阻塞，除非这个元素被另一个线程消费、
 * <p>
 * TODO: 阻塞队列的常见方法
 * 方法\方式	 抛出异常	返回特殊值	一直阻塞	超时退出
 * 插入方法	 add(e)	    offer(e)	put(e)	offer(e,time,unit)
 * 移除方法	 remove()	poll()	    take()	poll(time,unit)
 * 检查方法	 element()	peek()	    不可用	不可用
 * <p>
 * TODO:后进先出的队列、--> 双端(向)队列, 可以完成后进先出功能！
 * 目前默认的队列都是先进先出的模式, 可以通过继承{@link LinkedBlockingDeque}来实现后进先出。
 * 原理就是将take()和poll()方法重写一下, 分别调用takeLast()和pollLast()。
 * <p>
 * TODO: 无界阻塞队列不是真的无界
 * LinkedBlockingQueue阻塞队列大小的配置是可选的, 初始化时指定一个大小, 它就是有边界的, 否则它就是无边界的。
 * 说是无边界, 其实是采用了默认大小为Integer.MAX_VALUE(2147483647)的容量。
 *
 * @author 小66
 * @date 2020-07-04 20:07
 **/
public class BlockingQueueDemo {
    private static final Random RANDOM = new Random();
    private static final int LENGTH = 30;

    public static void main(String[] args) {
        testArrayBlockingQueue();
        // testLinkedBlockingQueue();
        // testLinkedBlockingDeque();
        // testPriorityBlockingQueue();
    }

    private static void testLinkedBlockingDeque() {
        // 不给capacity、就是Integer.MAX_VALUE的默认
        LinkedBlockingDeque<Integer> blockingQueue = new LinkedBlockingDeque<>();
        for (int i = 0; i < LENGTH; i++) {
            boolean offer = blockingQueue.offer(RANDOM.nextInt(LENGTH));
            System.out.println("offer = " + offer);
        }
        int size = blockingQueue.size();
        System.out.println("blockingQueue = " + blockingQueue);
        System.out.println("size = " + size);
        Integer poll = blockingQueue.poll();
        System.out.println("poll = " + poll);
        Integer element = blockingQueue.element();
        Integer peek = blockingQueue.peek();
        System.out.println("peek = " + peek);
        System.out.println("element = " + element);
        // push(e)->底层调用offerFirst(e)方法、
        blockingQueue.push(35);
        boolean offerFirst = blockingQueue.offerFirst(50);
        System.out.println("offerFirst = " + offerFirst);
        System.out.println("blockingQueue = " + blockingQueue);
        Integer pollLast = blockingQueue.pollLast();
        System.out.println("pollLast = " + pollLast);

    }

    private static void testLinkedBlockingQueue() {
        // 不给capacity、就是Integer.MAX_VALUE的默认
        BlockingQueue<Integer> blockingQueue = new LinkedBlockingQueue<>(20);
        for (int i = 0; i < LENGTH; i++) {
            boolean offer = blockingQueue.offer(RANDOM.nextInt(LENGTH));
            System.out.println("offer = " + offer);
        }
        int size = blockingQueue.size();
        System.out.println("blockingQueue = " + blockingQueue);
        System.out.println("size = " + size);
        Integer poll = blockingQueue.poll();
        System.out.println("poll = " + poll);
        Integer element = blockingQueue.element();
        Integer peek = blockingQueue.peek();
        System.out.println("peek = " + peek);
        System.out.println("element = " + element);
    }

    private static void testArrayBlockingQueue() {
        BlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<>(20);
        for (int i = 0; i < LENGTH; i++) {
            boolean offer = blockingQueue.offer(RANDOM.nextInt(LENGTH));
            System.out.println("offer = " + offer);
        }
        int size = blockingQueue.size();
        System.out.println("blockingQueue = " + blockingQueue);
        System.out.println("size = " + size);
        Integer poll = blockingQueue.poll();
        System.out.println("poll = " + poll);
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < blockingQueue.size(); i++) {
            // 模拟并发消费队列、--> 得到结果是依次消费的队列内容。(阻塞, ReentrantLock)
            executor.execute(() -> {
                Integer concurrentPoll = blockingQueue.poll();
                System.out.println("concurrentPoll = " + concurrentPoll);
            });
        }
        executor.shutdown();
    }

    /**
     * TODO: 优先级队列不保证排序, 它只保证头是按照排序规则的第一个, 但是在以下任何一个节点上都不保证排序。 只保证队列的第一个元素是最小/最大的！
     * {@link PriorityBlockingQueue#tryGrow(Object[], int)}实现基于CAS和自旋锁完成自动扩容、
     * TODO: 入队元素 必须实现 Comparable 接口, 否则报错
     */
    private static void testPriorityBlockingQueue() {
        BlockingQueue<Integer> blockingQueue = new PriorityBlockingQueue<>(20,
                Comparator.comparing(i -> i, Comparator.reverseOrder()));
        for (int i = 0; i < LENGTH; i++) {
            // add()/offer()底层都会调用tryGrow()方法完成自动扩容！
            boolean offer = blockingQueue.offer(RANDOM.nextInt(LENGTH));
            System.out.println("offer = " + offer);
        }
        int size = blockingQueue.size();
        System.out.println("blockingQueue = " + blockingQueue);
        System.out.println("size = " + size);
        Integer poll = blockingQueue.poll();
        System.out.println("poll = " + poll);
        // TODO：取完一个数据后, 队列会完成重排！
        System.out.println("blockingQueue After Poll = " + blockingQueue);
    }

    public static void testPriorityBlockingQueueTow() {
        // 入队元素 必须实现 Comparable 接口, 否则报错
        PriorityBlockingQueue<Person> priorityQueue = new PriorityBlockingQueue<>();
        for (int i = 0; i < LENGTH; i++) {
            // add()/offer()底层都会调用tryGrow()方法完成自动扩容！
            Person person = new Person();
            person.setAge(RANDOM.nextInt(LENGTH));
            boolean offer = priorityQueue.offer(person);
            System.out.println("offer = " + offer);
        }

        System.out.println("priorityQueue = " + priorityQueue);
        Person poll = priorityQueue.poll();
        System.out.println("poll = " + poll);
    }

    private static class Person {
        private Integer age;

        public Integer getAge() {
            return age;
        }

        void setAge(Integer age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "age=" + age;
        }
    }

}
