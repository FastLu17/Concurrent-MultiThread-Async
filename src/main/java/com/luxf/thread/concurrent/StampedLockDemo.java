package com.luxf.thread.concurrent;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.StampedLock;

/**
 * 正常情况下：写锁的优先级要高于读锁！
 * <p>
 * Java 8引入了新的读写锁：{@link StampedLock}, 进一步提升并发执行效率。
 * {@link StampedLock}和{@link ReadWriteLock}相比，改进之处在于：读的过程中也允许获取写锁后写入！
 * 我们读的数据就可能不一致，所以，需要一点额外的代码来判断读的过程中是否有写入。
 * <p>
 * 这种读锁是一种<B>乐观锁</B>。
 * <p>
 * StampedLock提供了乐观读锁，可取代ReadWriteLock以进一步提升并发性能；
 * <p>
 * 但是：StampedLock是不可重入锁。
 *
 * @author 小66
 * @date 2020-07-02 19:17
 **/
public class StampedLockDemo {
    private StampedLock stampedLock = new StampedLock();

    class Point {
        private final StampedLock stampedLock = new StampedLock();

        private double x;
        private double y;

        public void move(double deltaX, double deltaY) {
            long stamp = stampedLock.writeLock(); // 获取写锁
            try {
                x += deltaX;
                y += deltaY;
            } finally {
                stampedLock.unlockWrite(stamp); // 释放写锁
            }
        }

        public double distanceFromOrigin() {
            /**
             * 获取乐观锁、tryOptimisticRead()返回的是版本号，不是锁，根本没有锁。因此, 不需要释放锁！
             *
             * 后面validate()就是为了验证在这段时间内版本号变了没，如果没变，那就没有写入
             *
             * 版本号就是个long
             *
             * readLock()才返回真正的读锁，必须finally中unlock
             */
            long stamp = stampedLock.tryOptimisticRead(); // 获得一个乐观读锁
            // 注意下面两行代码不是原子操作
            // 假设x,y = (100,200)
            double currentX = x;
            // 此处已读取到x=100，但x,y可能被写线程修改为(300,400)
            double currentY = y;
            // 此处已读取到y，如果没有写入，读取是正确的(100,200)
            // 如果有写入，读取是错误的(100,400)
            /**
             * 检查乐观读锁后是否有其他写锁发生、
             * TODO: 显然乐观锁的并发效率更高，但一旦有小概率的写入导致读取的数据不一致，需要能检测出来，再读一遍就行
             */
            if (!stampedLock.validate(stamp)) {
                stamp = stampedLock.readLock(); // 获取一个悲观读锁
                try {
                    currentX = x;
                    currentY = y;
                } finally {
                    stampedLock.unlockRead(stamp); // 释放悲观读锁
                }

                /*// 如果验证不通过, 改为递归调用会是什么情况？
                return distanceFromOrigin();*/
            }
            return Math.sqrt(currentX * currentX + currentY * currentY);
        }
    }
}
