package me.athulsib.stomcheat.thread;

import lombok.Getter;

import java.util.concurrent.ExecutorService;

@Getter
public class Thread {
    private final ExecutorService executorService;
    private final String name;

    public Thread(ExecutorService executorService, String name) {
        this.executorService = executorService;
        this.name = name;
    }

    public void execute(Runnable runnable) {
        this.executorService.execute(runnable);
    }
}