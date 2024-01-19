package org.jingtao8a.async;

import org.jingtao8a.util.threadpool.ThreadPoolFactoryUtil;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsyncCallBackExecutor {
    private static final int worker = 4;
    private static final ThreadPoolExecutor callBackExecutor = new ThreadPoolExecutor(
            worker, worker, 2000L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            ThreadPoolFactoryUtil.createThreadFactory("org.jingtao8a.client.AsyncCallBackExecutor", false));
    public static void executor(Runnable runnable) {
        callBackExecutor.execute(runnable);
    }
}
