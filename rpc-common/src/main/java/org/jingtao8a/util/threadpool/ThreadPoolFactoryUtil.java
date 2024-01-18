package org.jingtao8a.util.threadpool;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.shaded.com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class ThreadPoolFactoryUtil {
    /**
     * 通过 threadNamePrefix 来区分不同线程池（我们可以把相同 threadNamePrefix 的线程池看作是为同一业务场景服务）。
     * key: threadNamePrefix
     * value: threadPool
     */
    private static final Map<String, ExecutorService> THREAD_POOLS = new ConcurrentHashMap<>();
    private ThreadPoolFactoryUtil() {}//该类不可实例化

    public static ExecutorService createCustomThreadPoolIfAbsent(String threaNamePrefix) {
        ThreadPoolConfig threadPoolConfig = new ThreadPoolConfig();
        return createCustomThreadPoolIfAbsent(threaNamePrefix, threadPoolConfig);
    }

    public static ExecutorService createCustomThreadPoolIfAbsent(String threadNamePrefix, ThreadPoolConfig threadPoolConfig) {
        return createCustomThreadPoolIfAbsent(threadNamePrefix, threadPoolConfig, false);
    }

    public static ExecutorService createCustomThreadPoolIfAbsent(String threadNamePrefix, ThreadPoolConfig threadPoolConfig, Boolean daemon) {
        ExecutorService threadPool = THREAD_POOLS.computeIfAbsent(threadNamePrefix, k -> {
           return createThreadPool(threadPoolConfig, threadNamePrefix, daemon);
        });
        if (threadPool.isShutdown() || threadPool.isTerminated()) {
            THREAD_POOLS.remove(threadNamePrefix);
            threadPool = createThreadPool(threadPoolConfig, threadNamePrefix, daemon);
            THREAD_POOLS.put(threadNamePrefix, threadPool);
        }
        return threadPool;
    }

    public static void shutDownAllThreadPool() {
        log.info("call shutDownAllThreadPool method");
        THREAD_POOLS.entrySet().parallelStream().forEach(entry -> {
            ExecutorService executorService = entry.getValue();
            executorService.shutdown();
            log.info("shut down thread pool [{}] [{}]", entry.getKey(), executorService.isTerminated());
            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("Thread pool never terminated");
                executorService.shutdownNow();
            }
        });
    }

    private static ExecutorService createThreadPool(ThreadPoolConfig threadPoolConfig, String threadNamePrefix, Boolean daemon) {
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, daemon);
        return new ThreadPoolExecutor(threadPoolConfig.getCorePoolSize(), threadPoolConfig.getMaximumPoolSize(),
                threadPoolConfig.getKeepAliveTime(), threadPoolConfig.getUnit(), threadPoolConfig.getWorkQueue(),
                threadFactory);
    }

    public static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon) {
        if (threadNamePrefix != null) {
            if (daemon != null) {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").setDaemon(daemon).build();
            } else {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }
}
