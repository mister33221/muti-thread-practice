package org.example.lock;


import java.util.concurrent.locks.ReentrantLock;

// 模擬三個受朴元賣完50張票的例子
// 嘗試切換 lockWithUnFair 和 lockWithFair 來觀察結果
class Ticket {
    private int number = 50;
//    非公平鎖: 一個線程可以連續多次獲取鎖，預設是非公平鎖，因為已CPU的角度而言，非公平鎖的效率更高，會依照怎麼做最快的方式把需求做完
    ReentrantLock lockWithUnFair = new ReentrantLock();
//    公平鎖: 一個線程只能連續獲取一次鎖，多個線程按照申請鎖的順序來獲取鎖，先來後到的概念
    ReentrantLock lockWithFair = new ReentrantLock(true);

    public void sale() {
        lockWithUnFair.lock();
//        lockWithFair.lock();
        try {
            if (number > 0) {
                System.out.println(Thread.currentThread().getName() + "賣出第: \t" + (number--) + "\t 還剩下: " + number);
            }
        } finally {
            lockWithUnFair.unlock();
//            lockWithFair.unlock();
        }
    }

}

public class SaleTicketDemoForFairAndUnFairLock {

    public static void main(String[] args) {
        Ticket ticket = new Ticket();

        new Thread(() -> {
            for (int i = 1; i < 55; i++) {
                ticket.sale();
            }
        }, "a").start();

        new Thread(() -> {
            for (int i = 1; i < 55; i++) {
                ticket.sale();
            }
        }, "b").start();

        new Thread(() -> {
            for (int i = 1; i < 55; i++) {
                ticket.sale();
            }
        }, "c").start();

    }

}
