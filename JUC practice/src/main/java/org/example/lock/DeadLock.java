package org.example.lock;

// 死鎖: 多個線程互相抱著對方需要的資源，然後形成僵持
// 本測試啟動後，會發現 main 線程一直處於等待狀態，app一直不會停止。
public class DeadLock {

    public static void main(String[] args) {

            String lockA = "lockA";
            String lockB = "lockB";

            new Thread(new HoldLockThread(lockA, lockB), "ThreadAAA").start();
            new Thread(new HoldLockThread(lockB, lockA), "ThreadBBB").start();
    }

}

class HoldLockThread implements Runnable {

    private String lockA;
    private String lockB;

    public HoldLockThread(String lockA, String lockB) {
        this.lockA = lockA;
        this.lockB = lockB;
    }

    // 線程操作資源類
    @Override
    public void run() {
        synchronized (lockA) {
            System.out.println(Thread.currentThread().getName() + "\t 自己持有: " + lockA + "\t 嘗試獲取: " + lockB);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            synchronized (lockB) {
                System.out.println(Thread.currentThread().getName() + "\t 自己持有: " + lockB + "\t 嘗試獲取: " + lockA);
            }
        }
    }
}
