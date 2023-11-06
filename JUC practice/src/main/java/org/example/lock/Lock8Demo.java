package org.example.lock;

import java.util.concurrent.TimeUnit;


class Phone {

    // 嘗試修改此方法 為 有無static、有無synchronized
    public synchronized  void sendEmail() {
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("-------sendEmail");
    }

    // 嘗試修改此方法 為 有無static、有無synchronized
    public synchronized  void sendSMS() {
        System.out.println("-------sendSMS");
    }

    public void hello() {
        System.out.println("-------hello");
    }

}

/**
 * 8種鎖的案例說明
 * 1. 標準訪問有a b兩個執行緒，請問先打印email還是sms
 * 2. 在sendEmail方法新增Thread.sleep(4000)，請問先打印email還是sms
 * 3. 新增一個普通方法hello，請問先打印email還是hello
 * 4. 有兩部手機，請問先打印email還是sms
 * 5. 兩個靜態同步方法，同一部手機，請問先打印email還是sms
 * 6. 兩個靜態同步方法，兩部手機，phone1 sendEmail、phone2 sendSMS，請問先打印email還是sms
 * 7. 一個靜態同步方法，一個普通同步方法，同一部手機，請問先打印email還是sms
 * 8. 一個靜態同步方法，一個普通同步方法，兩部手機，phone1 sendEmail、phone2 sendSMS，請問先打印email還是sms
 */

/**
 * 預期筆記
 * 1-2.
 *  一個對象裡面如果有多個synchronized方法，某一個時刻內，只要有一個執行緒去調用其中的一個synchronized方法了，
 *  其他的執行緒都只能等待，也就是說，某一個時刻內，只能有一個執行緒去調用這些synchronized方法。
 *  換句話說，synchronized鎖的是當前對象this，被鎖定後，其他的執行緒都不能進入到當前對象的其他synchronized方法。
 * 3-4.
 *  加個普通方法發現和同步鎖無關，因為並沒有去爭搶資源
 *  換成兩個對象，synchronized鎖的this是不同的對象，所以不會有爭搶資源的問題
 * 5-6. 都換成靜態同步方法
 *  由於在class中的方法加上了靜態static，那摩說明該方法的synchronized是鎖在class上的，而不是被new出來的對象上
 *  也就是說，不管你new幾個物件，只要是同一個class，那麼他們就是同一把鎖
 * 7-8.
 *  當一個執行緒試圖訪問同步方法時，他首先必須獲得鎖，退出或者抛出異常時必須釋放鎖
 *  所有的普通同步方法都是用的同一把鎖——實例對象本身，就是new出來的對象，就是this
 *  也就是說如果一個實例對物件的痛不方法獲取鎖後，該實例對象的其他同步方法必須等待獲取鎖的方法釋放鎖後才能獲取鎖
 *
 *  所有的近太同步方法用的也是同一把鎖——類對象本身，就是我們這裡說的Phone.class
 *  具體實例對象this和類對象class是兩個不同的對象，所以他們用的是兩把不同的鎖，一個是this，一個是class
 *  但是一但一個近太同步方法獲取鎖後，其他的近太同步方法都必須等待該方法釋放鎖後才能獲取鎖
 *
 */
public class Lock8Demo {

    public static void main(String[] args) {

        Phone phone1 = new Phone();
        Phone phone2 = new Phone();

        // 常是修改以下方法，以phone1、phone2呼叫有不同修飾詞的方法(如1-8的案例)，觀察結果，是否符合預期筆記
        new Thread(() -> {
            phone1.sendEmail();
        }, "A").start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            phone1.sendSMS();
        }, "B").start();

        new Thread(() -> {
            phone1.hello();
        }, "C").start();

        new Thread(() -> {
            phone2.sendSMS();
        }, "D").start();

    }

}


