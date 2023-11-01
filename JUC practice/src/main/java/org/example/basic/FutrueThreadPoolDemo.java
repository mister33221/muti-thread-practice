package org.example.basic;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class FutrueThreadPoolDemo {

    public static void main(String[] args) {

//        oneThread();
//        threeThreads();
        useFutureTaskForThreeThreads();

    }

    public static void oneThread() {

//        三個任務，目前只有一個線程 main 來處理，耗時多久?

//        1. start
        long start = System.currentTimeMillis();

//        2. tasks
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(1000);
                System.out.println("Hello from thread " + i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

//        3. end
        long end = System.currentTimeMillis();

//        4. print result
        System.out.println("One thread 耗時: " + (end - start) + " ms");
    }

    //    直接創建了三個新的線程來執行任務。這種方法更為直接，但是缺乏對線程生命週期的控制，並且不能獲取任務的結果。
    public static void threeThreads() {

//        三個任務，用三個線程來處理，耗時多久?

//        1. start
        long start = System.currentTimeMillis();

//        2. tasks
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    System.out.println("Hello from thread " + Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

//        3. end
        long end = 0;
        while (Thread.activeCount() > 2) {
            end = System.currentTimeMillis();
        }

//        4. print result
        System.out.println("Three threads 耗時: " + (end - start) + " ms");
    }

//    使用了FutureTask和ExecutorService。FutureTask是一種可以取消的異步計算任務，
//    它實現了Runnable和Future接口。ExecutorService是一種服務，它可以管理和控制線程的生命週期，
//    包括創建、啟動、關閉線程等。在這段程式碼中，每個任務都被包裝成一個FutureTask，然後被提交到ExecutorService來執行。
    public static void useFutureTaskForThreeThreads() {

//        三個任務，用三個線程來處理，耗時多久?

//        1. start
        long start = System.currentTimeMillis();

//        2. tasks
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        FutureTask<String> futureTask1 = new FutureTask<>(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("Hello from thread " + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Hello from thread " + Thread.currentThread().getName() + " is done";
        });
        executorService.submit(futureTask1);

        FutureTask<String> futureTask2 = new FutureTask<>(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("Hello from thread " + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Hello from thread " + Thread.currentThread().getName() + " is done";
        });
        executorService.submit(futureTask2);

        FutureTask<String> futureTask3 = new FutureTask<>(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("Hello from thread " + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Hello from thread " + Thread.currentThread().getName() + " is done";
        });
        executorService.submit(futureTask3);

        executorService.shutdown(); //ExecutorService的shutdown方法並不會立即終止執行中的任務，而是會禁止新的任務被提交到執行器服務。所有已經提交的任務都會被執行完畢。

//        3. end
        // 如果使用futureTask.get()，則會阻塞等待任務執行完畢，所以通常會放在最後面
        long end = 0;
        while (!futureTask1.isDone() || !futureTask2.isDone() || !futureTask3.isDone()) {
            end = System.currentTimeMillis();
        }
//        4. print result
        System.out.println("Three use FutureTask 耗時: " + (end - start) + " ms");

    }

}
