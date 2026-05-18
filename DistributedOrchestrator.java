package br.com.vhalinor.iag.orchestration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class DistributedOrchestrator {
    private static final Logger log = LoggerFactory.getLogger(DistributedOrchestrator.class);
    private final ForkJoinPool pool;
    private final ConcurrentHashMap<String, CompletableFuture<Object>> tasks = new ConcurrentHashMap<>();

    public DistributedOrchestrator(int parallelism) {
        this.pool = new ForkJoinPool(parallelism);
    }

    public String submit(Supplier<Object> task, int priority) {
        String taskId = UUID.randomUUID().toString().substring(0, 8);
        CompletableFuture<Object> future = CompletableFuture.supplyAsync(task, pool);
        tasks.put(taskId, future);
        future.exceptionally(ex -> {
            log.error("Task {} falhou: {}", taskId, ex.getMessage());
            return null;
        });
        return taskId;
    }

    public Object awaitResult(String taskId, long timeout, TimeUnit unit) throws TimeoutException, ExecutionException, InterruptedException {
        CompletableFuture<Object> future = tasks.get(taskId);
        if (future == null) throw new IllegalArgumentException("Task não encontrada");
        return future.get(timeout, unit);
    }

    public Map<String, Object> awaitAll(long timeout, TimeUnit unit) {
        // implementação com allOf
        return null;
    }

    public void shutdown() {
        pool.shutdown();
    }
}