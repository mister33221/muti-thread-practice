package org.example.completeFuture;

import java.util.concurrent.*;

public class CompletableFuturePopularMethod {


    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {

//        get();
//        getWithTimeout();
//        join();
//        getNow(2); // parameter is how many seconds you want to wait, 1 second will get the default value, 2 seconds will get the result
//        complete(1); // parameter is how many seconds you want to wait, 1 second will get the default value, 2 seconds will get the result
//        thenApply();
//        handle();
//        thenAccept();
//        mixThenRunThenAcceptThenApply();
//        selectThreadPool();
//        combineTwoCompletableFuture();
    }


    /**
     * `
     * get result from completableFuture
     * get() is a blocking method, it will throw ExecutionException if the task is failed
     * get() is a blocking method, we can set a timeout for it
     * join() is a blocking method, it will throw CompletionException if the task is failed,and we can use getCause() to get the real exception, it's eaiser to find the root cause,
     * getNow() is a non-blocking method, it will return the result if the task is done, or it will return the default value
     * complete() is a non-blocking method, it will return true if the task is done, or it will return false and the task will be canceled, and set the result to the default value
     */
    public static void get() throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
                return "Hello from completableFuture";
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

//        note: get() is a blocking method, it will throw ExecutionException if the task is failed
        System.out.println(completableFuture.get());
    }

    public static void getWithTimeout() throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
                return "Hello from completableFuture";
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

//        note: get() is a blocking method, we can set a timeout for it
        System.out.println(completableFuture.get(1000, TimeUnit.MILLISECONDS));
    }

    public static void join() {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
                return "Hello from completableFuture";
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

//        note: join() is a blocking method, it will throw CompletionException if the task is failed,
//        and we can use getCause() to get the real exception, it's eaiser to find the root cause,
//        and it's won't force us to catch the exception
        System.out.println(completableFuture.join());
    }

    public static void getNow(Integer waitForSecond) throws InterruptedException {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
                return "Hello from completableFuture";
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

//        note: getNow() is a non-blocking method, it will return the result if the task is done,
//        or it will return the default value
        Thread.sleep(waitForSecond * 1000);
        System.out.println(completableFuture.getNow("getNow default value"));
    }

    public static void complete(Integer waitForSecond) throws InterruptedException {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
                return "Hello from completableFuture";
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

//        note: complete() is a non-blocking method, it will return true if the task is done,
//        or it will return false and the task will be canceled, and set the result to the default value
        Thread.sleep(waitForSecond * 1000);
        System.out.println(completableFuture.complete("complete default value") + " " + completableFuture.join());
    }


    /**
     * trigger a task when completableFuture is done
     * thenApply(): 這個方法在異步操作完成後，對結果進行轉換或計算，並返回一個新的 CompletableFuture。如果原始 CompletableFuture 完成時發生異常，則不會調用 thenApply 函數。
     * handle(): 這個方法在異步操作完成後，無論成功還是異常，都會被調用。它可以用來處理異常，或者在計算結果時考慮到可能的異常。
     */

    public static void thenApply() {

        CompletableFuture completableFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(1000);
                        return "Hello from completableFuture";
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).thenApply(s -> {
                    System.out.println("in thenApply 1");
                    return s + " thenApply 1 ";
                }).thenApply(s -> {
                    int i = 1 / 0; // comment or uncomment this line to see the difference between thenApply() and handle()
                    System.out.println("in thenApply 2");
                    return s + " thenApply 2";
                }).thenApply(s -> {
                    System.out.println("in thenApply 3");
                    return s + " thenApply 3";
                })
                .whenComplete((result, exception) -> {
                    if (exception == null) {
                        System.out.println("--------計算結果: " + result);
                    }
                }).exceptionally(exception -> {
                    System.out.println("--------exception: " + exception.getMessage());
                    return null;
                });

//        在Java中，執行緒可以分為守護執行緒（daemon threads）和非守護執行緒（non-daemon threads）
//        守護執行緒（Daemon Threads）：
//          守護執行緒是應用程序中的背景執行緒，它們的存在不會防止應用程序的結束。
//          當所有的非守護執行緒結束時，守護執行緒會自動終止，而不會等待它們完成。
//          守護執行緒通常用於執行應用程序的一些低優先級工作，例如垃圾回收等。
//          你可以將執行緒設置為守護執行緒，使用 setDaemon(true) 方法。
//
//        非守護(使用者)執行緒（Non-Daemon Threads or User Threads）：
//          非守護執行緒是應用程序的主要執行緒，它們的存在會防止應用程序的結束。
//          當所有的非守護執行緒結束時，應用程序才會結束。
//          通常，應用程序的主執行緒和其他主要邏輯執行緒都是非守護執行緒。

//        到底下這行"main thread is done" user thread 就已經做完，會導致 app結束，線程池也會被關閉，所以沒有等到上面的 completableFuture（Daemon Threads） 做完
//        有兩種解決方案
//        1. 自己開新的線程池，不要與預設的 forkJoinPool 混用
//        2. 在最後加上一個 join()，讓 main thread 等待 completableFuture（Daemon Threads） 做完
//        3. 在此讓 user threads 睡一下，讓 completableFuture（Daemon Threads） 有時間做完

        System.out.println("main thread is done");

        completableFuture.join();

    }

    public static void handle() {

        CompletableFuture completableFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(1000);
                        return "Hello from completableFuture";
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).handle((s,e) -> {
                    System.out.println("in handle 1");
                    return s + " handle 1 ";
                }).handle((s,e) -> {
                    int i = 1 / 0; // comment or uncomment this line to see the difference between thenApply() and handle()
                    System.out.println("in handle 2");
                    return s + " handle 2";
                }).handle((s,e)  -> {
                    System.out.println("in handle 3");
                    return s + " handle 3";
                })
                .whenComplete((result, exception) -> {
                    if (exception == null) {
                        System.out.println("--------計算結果: " + result);
                    }
                }).exceptionally(exception -> {
                    System.out.println("--------exception: " + exception.getMessage());
                    return null;
                });

        System.out.println("main thread is done");

        completableFuture.join();

    }

    /**
     * process and consume result from completableFuture
     * thenAccept(): 沒有返回值，只是對結果進行消耗，所以在console中Hello from completableFuture thenAccept 1  thenAccept 2 後，只會看到null thenAccept 3
     */
    public static void thenAccept() {

        CompletableFuture completableFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(1000);
                        return "Hello from completableFuture";
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).thenApply(s -> {
                    System.out.println("in thenAccept 1");
                    return s + " thenAccept 1 ";
                }).thenAccept(s -> {
                    System.out.println("in thenAccept 2");
                    System.out.println(s + " thenAccept 2");
                }).thenAccept(s -> {
                    System.out.println("in thenAccept 3");
                    System.out.println(s + " thenAccept 3");
                })
                .whenComplete((result, exception) -> {
                    if (exception == null) {
                        System.out.println("--------計算結果: " + result);
                    }
                }).exceptionally(exception -> {
                    System.out.println("--------exception: " + exception.getMessage());
                    return null;
                });

        System.out.println("main thread is done");

        completableFuture.join();

    }

    public static void mixThenRunThenAcceptThenApply() {

        // thenRun: 無返回值，只是在上一步操作完成後，做一些事情
        System.out.print(CompletableFuture.supplyAsync(() -> "Hello")
                .thenRun(() -> {})
                .join()
        ); // should print null

        // thenAccept: 沒有返回值，只是對結果進行消耗
        System.out.print(CompletableFuture.supplyAsync(() -> "Hello")
                .thenAccept(s -> {})
                .join()
        ); // should print null

        // thenApply: 有返回值，可以對結果進行轉換或計算
        System.out.print(CompletableFuture.supplyAsync(() -> "Hello")
                .thenApply(s -> s + " world")
                .join()
        ); // should print Hello world

    }

    /**
     * select thread pool
     */
    public static void selectThreadPool() {

//        compare use thenRun() and thenRunAsync();

        ExecutorService es = Executors.newFixedThreadPool(3);

        CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(() -> "Hello")
                .thenRun(() -> System.out.println(Thread.currentThread().getName() + " thenRun"))
                .thenRunAsync(() -> System.out.println(Thread.currentThread().getName() + " thenRunAsync"))
                .thenRunAsync(() -> System.out.println(Thread.currentThread().getName() + " thenRunAsync"), es);

        completableFuture.join();

        es.shutdown();

        /*
        output: 可能會有兩種結果
        1.
        main thenRun 照理未指定線程池，所以用預設的 ForkJoinPool.commonPool(),但因為效能夠，底層會自動調用最優的線程，所以就直接使用main了
        ForkJoinPool.commonPool-worker-1 thenRunAsync
        pool-1-thread-1 thenRunAsync
        2.
        ForkJoinPool.commonPool-worker-1 thenRun 未指定線程池，所以用預設的 ForkJoinPool.commonPool()
        ForkJoinPool.commonPool-worker-1 thenRunAsync 未指定線程池，所以用預設的 ForkJoinPool.commonPool()
        pool-1-thread-1 thenRunAsync
         */

    }

    /**
     * combine two completableFuture
     */
    public static void combineTwoCompletableFuture() {

        CompletableFuture<String> completableFuture1 = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> completableFuture2 = CompletableFuture.supplyAsync(() -> " world");

        CompletableFuture<String> completableFuture3 = completableFuture1.thenCombine(completableFuture2, (s1, s2) -> s1 + s2);

        System.out.println(completableFuture3.join());

    }


}
