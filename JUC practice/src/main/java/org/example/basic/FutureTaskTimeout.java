package org.example.basic;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FutureTaskTimeout {

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {

        timeoutTest(5);

    }

    public static void timeoutTest(int timeout) throws ExecutionException, InterruptedException, TimeoutException {

        FutureTask<String> futureTask = new FutureTask<>(() -> {
            System.out.println("Hello from thread " + Thread.currentThread().getName());
            // 將此線程暫停5秒
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Task done";
        });
        // the second parameter is the return value of Thread.currentThread().getName()
        Thread t1 = new Thread(futureTask, "t1");
        t1.start();

        System.out.println("main thread is done");

        System.out.println(futureTask.get(3, TimeUnit.SECONDS));

    }

}
