package com.luxf.thread.async;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author 小66
 * @date 2020-07-03 22:09
 **/
@Service
public class AsyncService {

    /**
     * 使用{@link Async}时,如果需要方法的返回值、则可以通过{@link ListenableFuture#completable()}对象获取{@link CompletableFuture}、完成对返回值的异步操作
     * 如果使用{@link Future}对象, 则无法对返回值进行异步操作！
     *
     * <B>{@link Async#value()}</B>：指定异步线程池(taskExecutor->Bean对象在Spring容器中的名称)、
     * <p>
     * TODO: 注意、此处不能使用{@link AsyncResult}作为方法的返回值, 否则会抛出 ListenableFutureTask cannot be cast to AsyncResult 的异常！
     *
     * @return ListenableFuture
     */
    @Async(value = "asyncTaskExecutor")
    public ListenableFuture<String> getAsyncResult() {
        String name = Thread.currentThread().getName();
        System.out.println("threadName = " + name);
        // 执行具体需要进行的异步逻辑操作、并返回结果！
        String uuid = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new AsyncResult<>(uuid);
    }
}
