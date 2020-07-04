package com.luxf.thread.async;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

/**
 * @author 小66
 * @date 2020-07-03 22:11
 **/
@RestController
public class TestAsyncController {

    private final AsyncService asyncService;

    @Autowired
    public TestAsyncController(AsyncService asyncService) {
        this.asyncService = asyncService;
    }

    @GetMapping("/test")
    public void test() {
        ListenableFuture<String> asyncResult = asyncService.getAsyncResult();
        CompletableFuture<String> completable = asyncResult.completable();
        // 异步获取返回值、
        completable.whenCompleteAsync((s, throwable) -> {
            if (throwable == null) {
                // 一般这时候, 就需要通过 WebSocket 主动通知前端, 对前端页面更新数据！
                System.out.println("resultValue = " + s);
            }
        });
        System.out.println("true = " + true);
    }
}
