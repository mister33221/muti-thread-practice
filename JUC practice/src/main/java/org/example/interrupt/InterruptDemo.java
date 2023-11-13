package org.example.interrupt;

import java.util.concurrent.atomic.AtomicBoolean;

// 中斷: 一個線程正在執行，突然需要終止的情況，就叫做中斷，而線程的中斷不應該由別的線程去幫它中斷，而是由自己決定是否中斷，也因此產生出 volatile boolean 這個機制
// volatile是一個特殊的修飾符，主要用於確保變數的修改對所有線程都立即可見，以及禁止指令重排序。也可利用此特性，達成中斷的效果
// 利用atomic的方式，達成正確的中斷效果
// 使用interrupt，thread並不會立刻停止，而是會等到當前任務完成後，才會停止
public class InterruptDemo {

    static volatile boolean volatileStopFlag = false;

    static AtomicBoolean atomicBooleanStopFlag = new AtomicBoolean(false);

    public static void main(String[] args) {


//         interruptDemo();
//         interruptedDemo();
//        isIntrruptedDemo();
//        volatileDemo();
        atomicDemo();


    }

    public static void interruptDemo() {
        Thread thread = new Thread(() -> {
            System.out.println("thread start");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("thread end");
        });
        thread.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 中斷 thread
        thread.interrupt();
    }

    public static void interruptedDemo() {
        Thread thread = new Thread(() -> {
            System.out.println("thread start");
            while (true) {
                // 判斷當前線程是否被中斷
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("thread is interrupted");
                    break;
                }
            }
            System.out.println("thread end");
        });
        thread.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 中斷 thread
        thread.interrupt();
    }

    public static void isIntrruptedDemo() {
        Thread thread = new Thread(() -> {
            System.out.println("thread start");
            while (true) {
                // 判斷當前線程是否被中斷
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("thread is interrupted");
                    break;
                }
            }
            System.out.println("thread end");
        });
        thread.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 中斷 thread
        thread.interrupt();
    }

    public static void volatileDemo() {

        new Thread(() -> {
            System.out.println("thread start");
            while (true) {
                // 判斷當前線程是否被中斷
                if (volatileStopFlag) {
                    System.out.println("thread is interrupted");
                    break;
                }
                System.out.println("thread is running");
            }
            System.out.println("thread end");
        }).start();

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                volatileStopFlag = true;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }

    public static void atomicDemo() {

        new Thread(() -> {
            System.out.println("thread start");
            while (true) {
                // 判斷當前線程是否被中斷
                if (atomicBooleanStopFlag.get()) {
                    System.out.println("thread is interrupted");
                    break;
                }
                System.out.println("thread is running");
            }
            System.out.println("thread end");
        }).start();

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                atomicBooleanStopFlag.set(true);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }

}
