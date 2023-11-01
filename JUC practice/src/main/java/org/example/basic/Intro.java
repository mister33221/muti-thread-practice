package org.example.basic;

import java.util.concurrent.*;

public class Intro {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

//        createThread();
//        createThread2();
//        judgeDaemonThreadOrUserThread();
//        futureAPI();
        runableCallableFutureFutureTask();
    }

    public static void createThread() {

        Thread t1 = new Thread(() -> {
            System.out.println("Hello from thread 1");
        });

        t1.start();

//        start會調用start0，並將started設為true，代表已經啟動
//        start0是個native method，表示是用C寫的底層方法，也就是說這個方法是在非Java代碼中實現的，通常是在C或C++中。這樣的方法被稱為本地方法。
//        本地方法允許Java與作業系統或硬體進行交互，或者訪問系統級別的資源。在Thread類別中，start0方法是用來啟動一個新的執行緒的，這個操作涉及到底層的系統調用，因此需要在本地代碼中實現。

    }

    public static void createThread2() {
        Thread t1 = new Thread(() -> {
            System.out.println("Hello from thread 1");
        });
        t1.run();

        Object obj = new Object();

        new Thread(() -> {
            synchronized (obj) {
                System.out.println("Hello from thread 1");
            }
        }, "t2").start();


    }

    // 判斷微 user thread or daemon thread
    public static void judgeDaemonThreadOrUserThread() {
        Thread t1 = new Thread(() -> {
            System.out.println("Hello from thread 1");
            while (true) {

            }
        });
//        open this line, t1 will be daemon thread
//        when main thread is done, t1 will be terminated

//        comment this line, t1 will be user thread
//        main thread is done, but t1 is still running, so the program is not terminated.
//        t1.setDaemon(true);
        t1.start();

        System.out.println("main thread is done");

    }

    // Future API 用法
    public static void futureAPI() {
//         ExecutorService 是一個介面，定義了一些方法，可以用來管理和執行執行緒
//         Executors 是一個工具類，提供了一些靜態方法，用來創建不同類型的執行緒池
//         newFixedThreadPool 創建一個固定大小的執行緒池，並且每次提交一個任務就創建一個執行緒，直到達到最大的執行緒數，這時候會將提交的任務存入到等待隊列中
        ExecutorService executorService = Executors.newFixedThreadPool(1);
//        submit方法用來提交任務，並且返回一個Future，代表了將來要返回的結果
        Future<String> future = executorService.submit(() -> {
            Thread.sleep(5000);
            return "Hello from Callable, It will be returned after 5 seconds";
        });

        System.out.println("do something else");

//        透過future.get()方法可以獲取結果，如果任務還沒有執行完，則會阻塞等待
        try {
            String result = future.get();
            System.out.println(result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
    }

    // Runable, Callable, Future, FutureTask example
    public static void runableCallableFutureFutureTask() throws ExecutionException, InterruptedException {
        // Runnable example
        // Runnable是一個接口，它代表一個要由線程執行的任務
        Runnable runnableTask = () -> {
            System.out.println("Runnable task is running");
        };
        new Thread(runnableTask).start(); // 創建一個新的線程來執行Runnable任務

        // Callable and Future example
        // Callable是一個接口，它代表一個會返回結果的任務
        Callable<String> callableTask = () -> {
            Thread.sleep(1000);
            return "Callable task's result";
        };
        ExecutorService executorService = Executors.newSingleThreadExecutor(); // 創建一個單線程的ExecutorService
        Future<String> future = executorService.submit(callableTask); // 提交Callable任務並獲得一個Future
        System.out.println("Future result: " + future.get()); // 使用Future的get方法來獲取任務的結果

        // FutureTask example
        // FutureTask是一種可以取消的異步計算，它實現了Runnable和Future接口
        FutureTask<String> futureTask = new FutureTask<>(callableTask); // 創建一個FutureTask
        new Thread(futureTask).start(); // 創建一個新的線程來執行FutureTask
        System.out.println("FutureTask result: " + futureTask.get()); // 使用FutureTask的get方法來獲取任務的結果

        executorService.shutdown(); // 關閉ExecutorService
    }

}

