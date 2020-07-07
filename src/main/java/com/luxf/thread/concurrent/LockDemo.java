package com.luxf.thread.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * lock.tryLock()==true时, 就已获取到锁、如果再加锁, 其他就无法获取锁、
 * 想要其他线程还能正常获取锁, 就要多执行一次unlock()方法、
 * <p>
 * {@link LinkedBlockingQueue} 中有很好的关于{@link ReentrantLock}和{@link Condition}的使用方式、
 * <p>
 * TODO: 非阻塞的{@link Lock#tryLock()}方法并不遵循公平设置,它不管等待中的线程, 而是尽可能的立即获取锁！
 *
 * @author 小66
 * @date 2020-07-01 21:12
 **/
public class LockDemo {
    /**
     * {@link Lock}对象的方法很少, 只有6个、
     * {@link ReentrantLock}对象的特有方法很多、
     */
    private final static ReentrantLock LOCK = new ReentrantLock(true);
    /**
     * 利用Condition.await()、await(2,TimeUnit.SECONDS)和 signal()、signalAll()方法可以完成生产消费模式、
     * await(2,TimeUnit.SECONDS)：可以在等待超时后, 自己主动唤醒！
     * <p>
     * 可以多次调用lock.newCondition()方法创建多个condition对象，也就是一个lock可以持有多个等待队列。并发包中的Lock拥有一个同步队列和多个等待队列、
     * 当调用condition.await()方法后会使得当前获取lock的线程进入到等待队列，并解锁了等待队列中的线程！
     * {@link AbstractQueuedSynchronizer#unparkSuccessor(AbstractQueuedSynchronizer.Node)} 该方法最后执行--> LockSupport.unpark(s.thread);
     * 如果该线程能够从await()方法返回的话一定是该线程获取了与condition相关联的lock。
     *
     * TODO: 调用condition.await()方法的线程必须是已经获得了lock，也就是当前线程是同步队列中的头结点。
     */
    private final static Condition CONDITION = LOCK.newCondition();

    public static void main(String[] args) {
        LockDemo lockDemo = new LockDemo();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 5; i++) {
            executorService.execute(lockDemo::lockMethod);
//            executorService.execute(lockDemo::runLockMethod);
        }
    }

    public void lockMethod() {
        // lock()：阻塞方法、会一直等待！
        LOCK.lock();
        try {
            // CONDITION.await(); 必须在获得锁之后, 才可以调用！
//            while (true) {
//                CONDITION.await();
//            }
            System.out.println("System.currentTimeMillis() = " + System.currentTimeMillis());
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            LOCK.unlock();
        }
    }

    public void runLockMethod() {
        // lock对象调用tryLock(long time, TimeUnit unit)方法尝试获取锁、 到时间后,就不再阻塞、
        try {
            String threadName = Thread.currentThread().getName();
            // 注意, 这个方法需要抛出中断异常
            if (LOCK.tryLock(5, TimeUnit.SECONDS)) {
                // 获锁成功代码段
                System.out.println("线程" + threadName + "获取锁成功");
                /**
                 * {@link ReentrantLock#isLocked()}：isLocked()方法就是ReentrantLock特有的方法！
                 */
                boolean locked = LOCK.isLocked();
                System.out.println("locked = " + locked);
                // tryLock()==true时, 就已获取到锁、如果再加锁, 其他就无法获取锁、 想要其他线程还能正常获取锁, 就要多执行一次unlock()方法、
                // LOCK.lock();
                try {
                    // 执行的代码
                    Thread.sleep(3000);
                } finally {
                    // 获取锁成功之后, 一定记住加finally并unlock()方法,释放锁
                    LOCK.unlock();
                    System.out.println("线程" + threadName + "释放锁");
                }
            } else {
                // 获锁失败代码段, 具体获取锁失败的回复响应
                System.out.println("线程" + threadName + "获取锁失败");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
