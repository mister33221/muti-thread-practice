package org.example.completeFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RunAsyncAndSupplyAsync {

    public static void main(String[] args) {

//        runAsync 及 supplyAsync 都是會產生一個新的 thread，去做裡面的事情，他們的差別在於
//        runAsync 是沒有回傳值的，所以他的參數是 Runnable
//        supplyAsync 是有回傳值的，所以他的參數是 Supplier
        useRunAsync();
        useSupplyAsync();

    }

    public static void useRunAsync() {

        System.out.println("1. useRunAsync");

//         runAsync use Runnable, so there is no return value
//         if you don't use specify the thread pool, it will use ForkJoinPool.commonPool()
//         commpletebleFuture: 是一個非同步的工具，可以讓你在一個thread裡面做完一件事情之後，再去做另一件事情
//          所以在這行 CompletableFuture.runAsync 時，就會開一個新的thread，去做裡面的事情，而 main thread 就會繼續往下做
//        所以會先印出 "main thread is done brfore join line"，再印出 "Hello from RunAsync thread who use ForkJoinPool.commonPool-worker-1"
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                System.out.println("3. Hello from RunAsync thread who use" + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).whenComplete((aVoid, throwable) -> {
            System.out.println("4. runAsync in whenComplete");
        });

        System.out.println("2. main thread is done brfore join line");
        // join() 是一個blocking method，會等到future裡面的事情做完之後，才會繼續往下做，所以會等到上面的future做完之後，才會印出下面的字
        future.join();

        System.out.println("5. after join line");

        // 沒有指定線程池的話，會用 ForkJoinPool.commonPool()，且在 main thread 執行完之後，就會自動關閉

    }

    public static void useSupplyAsync() {

        System.out.println("1. useSupplyAsync");

            ExecutorService es = Executors.newFixedThreadPool(3);

        // supplyAsync use Supplier, so there is a return value
        // if you use specify the thread pool, it will a new thread pool
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                System.out.println("3. Hello from thread who use" + Thread.currentThread().getName());
                return "5. SupplyAsync task done";
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, es).whenComplete((s, throwable) -> {
            System.out.println("4. supplyAsync in whenComplete");
        }).exceptionally(throwable -> {
            System.out.println("exception happened");
            return null;
        });

        System.out.println("2. main thread is done before join line");

        System.out.println(future.join());

        System.out.println("6. after join line");

//        有指定線程池的話，會用指定的線程池，且在 main thread 執行完之後，不會自動關閉，所以要自己關閉
        es.shutdown();

    }

}
