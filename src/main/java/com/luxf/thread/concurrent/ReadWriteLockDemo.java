package com.luxf.thread.concurrent;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 正常情况下：写锁的优先级要高于读锁！
 * <p>
 * TODO: 非阻塞的{@link Lock#tryLock()}方法并不遵循公平设置,它不管等待中的线程, 而是尽可能的立即获取锁！
 * <p>
 * 尝试获取一个公平读锁(非重入的)的线程会因为写锁被持有或者存在一条等待的写线程而被阻塞！在写锁线程未释放写锁之前,没有办法获取到读锁！
 * <p>
 * 写入锁是互斥的、读锁可能会被多条线程同时持有(测试没有出现多条线程持有读锁)！
 * 如果有线程正在读, 写线程需要等待读线程释放锁后才能获取写锁, 即读的过程中不允许写，这是一种<B>悲观的读锁</B>。
 * 允许多个线程在没有写入时同时读取！
 * <p>
 * {@link ReadWriteLock} 适用于对数据频繁读取而较少修改的场景！
 * <p>
 *
 * TODO: 读锁允许了多线程运行，但是如果我们对读操作不加锁，因为写操作会加锁，能不能少些一点儿代码？效果也是一样的呢？
 *  针对这个问题：
 *  <B>锁的目的不是读的数据是错的，加锁的目的是保证逻辑上一致的(逻辑正确)！</B>
 *  int x = obj.x;
 *  // 这里线程可能中断
 *  int y = obj.y;
 *  假设obj的x，y是[0,1]，某个写线程修改成[2,3]，你读到的要么是[0,1]，要么是[2,3]，但是没有锁，你读到的可能是[0,3]
 *
 * @author 小66
 * @date 2020-07-02 18:07
 **/
public class ReadWriteLockDemo {
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);
    private static final ReentrantReadWriteLock READ_WRITE_LOCK = new ReentrantReadWriteLock(true);
    private static final String[] WORDS = {"A", "B", "C", "D", "E", "F", "G"};
    private static final String[] DETAILS = {"字母A-a", "字母B-b", "字母C-c", "字母D-d", "字母E-e", "字母F-f", "字母G-g"};
    private static final ConcurrentHashMap<String, String> CACHE = new ConcurrentHashMap<>();

    private static final CyclicBarrier CYCLIC_BARRIER = new CyclicBarrier(2);

    public static void main(String[] args) {
        // 利用CyclicBarrier, writeMethod()和readMethod()同时执行任务、
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(ReadWriteLockDemo::writeMethod);
        executorService.submit(ReadWriteLockDemo::readMethod);
        executorService.shutdown();
    }

    public static void writeMethod() {
        try {
            System.out.println("writeMethod 到达栅栏！");
            CYCLIC_BARRIER.await();
            System.out.println("writeMethod 冲破栅栏！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ReentrantReadWriteLock.WriteLock writeLock = READ_WRITE_LOCK.writeLock();
        ExecutorService writePool = Executors.newFixedThreadPool(1);
        for (int i = 0; i < WORDS.length; i++) {
            int finalI = i;
            writePool.submit(() -> {
                int readLockCount = READ_WRITE_LOCK.getReadLockCount();
                // 测试没有出现 readLockCount>1 的情况
                if (readLockCount == 1) {
                    System.out.println("readLockCount = " + readLockCount);
                }
                writeLock.lock();
                try {
                    CACHE.putIfAbsent(WORDS[finalI], DETAILS[finalI]);
                    System.out.println("writeLock put " + WORDS[finalI]);
                } finally {
                    writeLock.unlock();
                }
            });
        }
        writePool.shutdown();
    }

    public static void readMethod() {
        try {
            System.out.println("readMethod 到达栅栏！");
            CYCLIC_BARRIER.await();
            System.out.println("readMethod 冲破栅栏！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ReentrantReadWriteLock.ReadLock readLock = READ_WRITE_LOCK.readLock();
        ExecutorService readPool = Executors.newFixedThreadPool(30);
        readPool.submit(() -> {
            while (ATOMIC_INTEGER.getAndIncrement() < 30000) {
                readLock.lock();
                try {
                    int nextInt = new Random().nextInt(WORDS.length);
                    System.out.println("readLock get '" + WORDS[nextInt] + "' = " + CACHE.get(WORDS[nextInt]));
                } finally {
                    readLock.unlock();
                }
            }
        });
        readPool.shutdown();
    }
}
