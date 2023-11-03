package org.example.atonic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class TicketService {

    private Semaphore semaphore;

    public TicketService(int initialCount) {
        this.semaphore = new Semaphore(initialCount);
    }

    public boolean purchaseTicket() {
        if (semaphore.tryAcquire()) {
            // 購票成功
            return true;
        } else {
            // 票已售罄
            return false;
        }
    }

    public static void main(String[] args) {

        // init time
        long start = System.currentTimeMillis();

        // 假設初始有10張票
        TicketService ticketService = new TicketService(10);

        // 假設有10000個請求同時搶票
        ExecutorService executorService = Executors.newFixedThreadPool(10000);
        for (int i = 0; i < 10000; i++) {

            int finalI = i;

            executorService.submit(() -> {
                if (ticketService.purchaseTicket()) {
                    System.out.println("No."+ finalI + " " + Thread.currentThread().getName() + " 購票成功");
                } else {
                    System.out.println("No."+ finalI + " " + Thread.currentThread().getName() + " 票已售罄");
                }
            });
        }

        executorService.shutdown();

        // end time
        long end = System.currentTimeMillis();

        System.out.println("耗時: " + (end - start) + " ms");
    }

}
