package org.example.lock;

import java.util.concurrent.TimeUnit;

public class LockSyncDemo {

//    用類別new出一個物件(實例)
    Object objectLockA = new Object();

//    將synchronized放在 objectLockA 上，代表當要執行synchronized (objectLockA) {}裡面的程式時，
//    必須先獲取objectLockA的鎖，同一個時間只能有一個執行緒獲取到objectLockA的鎖，其他執行緒必須等待
    public void methodA() {
        synchronized (objectLockA) {
            System.out.println("methodA: synchronize on objectLockA");
        }
    }

//    將synchronized放在方法上，也等同於將synchronized放在this上，
//    也就是說，當你new出一個LockSyncDemo的物件時，只能有一個執行緒可以使用這個物件中帶有synchronized的方法，
//    而其他沒有帶有synchronized的方法，則可以被多個執行緒使用
    public synchronized void methodB() throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        System.out.println("methodB: synchronize on this");
    }

//    將synchronized放在static方法上，也等同於將synchronized放在class上，
//    也就是說，無論你new出多少個新的instance，只要是同一個class，那麼他們就是同一把鎖
    public static synchronized void methodC() throws InterruptedException {
//        TimeUnit.SECONDS.sleep(3);
        System.out.println("methodC: synchronize on class");
    }

    public static void main(String[] args) throws InterruptedException {

        LockSyncDemo lockSyncDemo = new LockSyncDemo();

        new Thread(()->{
            try{
            LockSyncDemo.methodC();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }).start();

        new Thread(()->{
            lockSyncDemo.methodA();
        }).start();

        new Thread(()->{
            try {
                lockSyncDemo.methodB();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();


        System.out.println("main thread is done");


    }

}
