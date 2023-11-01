package org.example.completeFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class runAsyncAndSupplyAsync {

    public static void main(String[] args) {

        useRunAsync();
        useSupplyAsync();

    }

    public static void useRunAsync() {

        System.out.println("useRunAsync");

        // runAsync use Runnable, so there is no return value
        // if you don't use specify the thread pool, it will use ForkJoinPool.commonPool()
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println("Hello from RunAsync thread who use" + Thread.currentThread().getName());
        });

        System.out.println("main thread is done");
        // join() is the same as get() but without checked exception
        future.join();

    }

    public static void useSupplyAsync() {

        System.out.println("useSupplyAsync");

        ExecutorService es = Executors.newFixedThreadPool(3);

        // supplyAsync use Supplier, so there is a return value
        // if you use specify the thread pool, it will a new thread pool
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("Hello from thread who use" + Thread.currentThread().getName());
            return "SupplyAsync task done";
        }, es);

        System.out.println("main thread is done");

        System.out.println(future.join());

        es.shutdown();

    }

}
