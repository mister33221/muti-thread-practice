package org.example.lock;


// 重入鎖是指同一個線程外層函數獲取鎖之後，內層遞迴函數仍然能獲取該鎖的代碼，在同一個線程在外層方法獲取鎖的時候，在進入內層方法會自動獲取鎖。
// synchronized: 隱式重入鎖，出現一次 synchronized 就會鎖住一次，鎖住的是同一個對象
// ReentrantLock: 顯式重入鎖，需要手動鎖住和解鎖，鎖住的是同一個對象

import java.util.concurrent.locks.ReentrantLock;

public class ReEntryLockDemo {

    public static void main(String[] args) {

        ReEntryLockDemo reEntryLockDemo = new ReEntryLockDemo();

//        註解掉一個方法，再執行另一個方法，觀察隱式重入鎖和顯式重入鎖的差異
//        reEntryLockDemo.testSynchronized();
        reEntryLockDemo.testReentrantLock();

    }

    public void testSynchronized() {
        final Object object = new Object();

        new Thread(() -> {

            synchronized (object) {
                System.out.println(Thread.currentThread().getName() + "\t synchronized-外層");
                synchronized (object) {
                    System.out.println(Thread.currentThread().getName() + "\t synchronized-中層");
                    synchronized (object) {
                        System.out.println(Thread.currentThread().getName() + "\t synchronized-內層");
                    }
                }
            }

        }).start();
    }

    public void testReentrantLock() {

        ReentrantLock lock = new ReentrantLock();

        new Thread(() -> {

            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + "\t ReentrantLock-外層");
                lock.lock();
                try {
                    System.out.println(Thread.currentThread().getName() + "\t ReentrantLock-中層");
                    lock.lock();
                    try {
                        System.out.println(Thread.currentThread().getName() + "\t ReentrantLock-內層");
                    } finally {
                        lock.unlock();
                    }
                } finally {
                    lock.unlock();
                }
            } finally {
//                嘗試註解這行，導致t1沒有解鎖，t2無法獲取鎖，造成死鎖
                lock.unlock();
            }

        }, "t1").start();

        new Thread(() -> {

            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + "\t ReentrantLock-外層");
            } finally {
                lock.unlock();
            }

        }, "t2").start();

    }
}
