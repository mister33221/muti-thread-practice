package org.example.atonic;

import java.util.concurrent.atomic.AtomicInteger;

public class Atocic {

    public static void main(String[] args) {

        noAtonicApi();
        atonicApi();

    }

    public static void noAtonicApi(){
        final NonAtomicCounter counter = new NonAtomicCounter();

        Runnable incrementTask = () -> {
            for (int i = 0; i < 50000; i++) {
                counter.increment();
            }
        };

        Thread thread1 = new Thread(incrementTask);
        Thread thread2 = new Thread(incrementTask);

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("noAtonicApi final count: " + counter.increment());
    }

    public static void atonicApi(){
        final AtomicInteger counter = new AtomicInteger(0);

        Runnable incrementTask = () -> {
            for (int i = 0; i < 50000; i++) {
                counter.incrementAndGet();
            }
        };

        Thread thread1 = new Thread(incrementTask);
        Thread thread2 = new Thread(incrementTask);

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("atonicApi final count: " + counter.get());
    }

}


class NonAtomicCounter {
    private int count = 0;

    public int increment() {
        int temp = count;  // 讀取當前計數值
        temp = temp + 1;   // 增加計數值
        count = temp;      // 將更新後的計數值寫回
        return temp;       // 返回更新後的計數值
    }
}