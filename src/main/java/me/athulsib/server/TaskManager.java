package me.athulsib.server;

import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.Task;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskManager {
    private final Scheduler scheduler;
    private final ExecutorService asyncExecutor;
    private final AtomicInteger taskCounter = new AtomicInteger(0);
    private volatile boolean shutdown = false;

    public TaskManager() {
        this.scheduler = MinecraftServer.getSchedulerManager();
        
        // Create a dedicated thread pool for async tasks
        int cores = Runtime.getRuntime().availableProcessors();
        this.asyncExecutor = new ThreadPoolExecutor(
            cores, 
            cores * 2, 
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new NamedThreadFactory("StomCheat-Async"),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    /**
     * Schedule an async task to run immediately
     * @param task The task to run
     * @return Task object that can be cancelled
     */
    public Task runAsync(Runnable task) {
        return runAsyncLater(task, 0);
    }

    /**
     * Schedule an async task to run after a delay
     * @param task The task to run
     * @param delay Delay in milliseconds
     * @return Task object that can be cancelled
     */
    public Task runAsyncLater(Runnable task, long delay) {
        checkShutdown();
        
        return scheduler.buildTask(() -> asyncExecutor.execute(task))
            .delay(delay, TimeUnit.MILLISECONDS.toChronoUnit())
            .schedule();
    }

    /**
     * Schedule a repeating async task
     *
     * @param task         The task to run
     * @param initialDelay Initial delay in milliseconds
     * @param period       Period between executions in milliseconds
     * @param milliseconds
     * @return Task object that can be cancelled
     */
    public Task runAsyncTimer(Runnable task, long initialDelay, long period, TimeUnit milliseconds) {
        checkShutdown();
        
        return scheduler.buildTask(() -> asyncExecutor.execute(task))
            .delay(initialDelay, TimeUnit.MILLISECONDS.toChronoUnit())
            .repeat(period, TimeUnit.MILLISECONDS.toChronoUnit())
            .schedule();
    }

    /**
     * Schedule a task to run on the main server thread
     * @param task The task to run
     * @return Task object that can be cancelled
     */
    public Task runSync(Runnable task) {
        checkShutdown();
        
        return scheduler.buildTask(task).schedule();
    }

    /**
     * Schedule a task to run on the main server thread after a delay
     * @param task The task to run
     * @param delay Delay in milliseconds
     * @return Task object that can be cancelled
     */
    public Task runSyncLater(Runnable task, long delay) {
        checkShutdown();
        
        return scheduler.buildTask(task)
            .delay(delay, TimeUnit.MILLISECONDS.toChronoUnit())
            .schedule();
    }

    /**
     * Schedule a repeating task on the main server thread
     * @param task The task to run
     * @param initialDelay Initial delay in milliseconds
     * @param period Period between executions in milliseconds
     * @return Task object that can be cancelled
     */
    public Task runSyncTimer(Runnable task, long initialDelay, long period) {
        checkShutdown();
        
        return scheduler.buildTask(task)
            .delay(initialDelay, TimeUnit.MILLISECONDS.toChronoUnit())
            .repeat(period, TimeUnit.MILLISECONDS.toChronoUnit())
            .schedule();
    }

    /**
     * Execute a task immediately in the async pool
     * @param task The task to run
     */
    public void executeAsync(Runnable task) {
        checkShutdown();
        asyncExecutor.execute(task);
    }

    /**
     * Submit a task to the async pool and get a Future
     * @param task The task to run
     * @param <T> Return type
     * @return Future representing the task
     */
    public <T> CompletableFuture<T> submitAsync(Callable<T> task) {
        checkShutdown();
        return CompletableFuture.supplyAsync(() -> {
            try {
                return task.call();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, asyncExecutor);
    }

    /**
     * Shutdown the task manager
     */
    public void shutdown() {
        shutdown = true;
        asyncExecutor.shutdown();
        try {
            if (!asyncExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                asyncExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            asyncExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void checkShutdown() {
        if (shutdown) {
            throw new IllegalStateException("TaskManager has been shut down");
        }
    }

    /**
     * Custom thread factory for naming threads
     */
    private static class NamedThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        NamedThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, namePrefix + "-" + threadNumber.getAndIncrement());
            t.setDaemon(true);
            return t;
        }
    }
}