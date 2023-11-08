package com.kai.ticketsystemformutithreadspractice.service;

import com.kai.ticketsystemformutithreadspractice.models.AddTicketsInfo;
import com.kai.ticketsystemformutithreadspractice.models.Ticket;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
public class TicketService {

//    票數限制
    private static final int TICKET_LIMIT = 2;

//    ConcurrentHashMap 是一種線執行緒安全的 HashMap，允許多個執行緒同時讀寫，且效率比 Hashtable 高。
//    在這邊為了確保Ticket在被讀寫時不會被其他人讀寫，所以使用ConcurrentHashMap來確保Ticket在被讀寫時不會被其他人讀寫。

    private ConcurrentHashMap<String, Ticket> tickets = new ConcurrentHashMap<>();

//    在這邊為了確保buyerTickets在被讀寫時不會被其他人讀寫，所以使用ConcurrentHashMap來確保buyerTickets在被讀寫時不會被其他人讀寫。
    private ConcurrentHashMap<String, Integer> buyerTickets = new ConcurrentHashMap<>();

//    AtomicInteger 是一種線執行緒安全的 Integer，允許多個執行緒同時讀寫，又可以確保在多個執行緒同時讀寫時，不會發生資料不一致的問題。
    private AtomicInteger ticketId = new AtomicInteger();

    // ReentrantLock 是一種可重入的鎖，可以確保當一個執行緒獲得鎖後，可以再次獲得該鎖，而不會被鎖阻塞。
    // Lock 是一種鎖的抽象類別，ReentrantLock 是 Lock 的一種實現。
    private Lock lock = new ReentrantLock();

//     Condition 是一種條件變數，可以讓執行緒在滿足某個條件時，進行等待，或者在某個條件滿足時，進行通知。
    private Condition ticketAvailable = lock.newCondition();

    public Object buyTicket(int type, String buyerId) throws InterruptedException {
//         version 1
//        1. 先查出該買家已經買了幾張票，如果已經買了兩張票，就不能再買了
        if (checkBuyerHasBought2OrMoreTickets(buyerId)) {
            return "You can't buy more than 2 tickets";
        }

//        2. 如果還沒買兩張票，就開始找票
        for (Ticket ticket : tickets.values()) {
//            3. 將票加上鎖，確保在這個ticket被購買時，其他人不能購買這張票
            synchronized (ticket) {
//                4. 再次確認該買家已經買了幾張票，必免在購買時，多個執行緒同時通過步驟1.1，導致買家買了超過兩張票
                if (checkBuyerHasBought2OrMoreTickets(buyerId)) {
                    return "You can't buy more than 2 tickets";
                }
//                5. 確認該票還未售出、且該票的種類符合購買者要求，都符合的話，就開始購買
                if (!ticket.getIsSold().get() && ticket.getType() == type) {
                    ticket.getIsSold().set(true);
                    ticket.setOwner(buyerId);
                    buyerTickets.put(buyerId, buyerTickets.getOrDefault(buyerId, 0) + 1);
                    return ticket;
                }
            }
        }

//        NOTE: 如果有確認有幾種票的話，可以將2-5的步驟切成不同的執行緒，針對不同票種進行迴圈，這樣可以提升效率

        return "No ticket available";
////        version 2 將整個方法上鎖
//        lock.lock();
//        try {
//            Integer count = buyerTickets.getOrDefault(buyerId, 0);
//            if (count >= 2) {
//                return "You can't buy more than 2 tickets";
//            }
//
//            for (Ticket ticket : tickets.values()) {
//                synchronized (ticket) {
//                    if (!ticket.getIsSold().get() && ticket.getType() == type) {
//                        ticket.getIsSold().set(true);
//                        ticket.setOwner(buyerId);
//                        buyerTickets.put(buyerId, count + 1);
//                        return ticket;
//                    }
//                }
//            }
//            return "No ticket available";
//        } finally {
//            lock.unlock();
//        }


//        return null;

    }

    public String returnTicket(String buyerId, String ticketId) {
//        version 1
//        Ticket ticket = tickets.get(ticketId);
//        if (ticket != null && ticket.getOwner().equals(buyerId)) {
//            synchronized (ticket) {
//                ticket.getIsSold().set(false);
//                ticket.setOwner(null);
//                return "Ticket returned successfully";
//            }
//        }
//        return "Ticket return failed";
//        verison 2
//        1. 取得該票
        Ticket ticket = tickets.get(ticketId);
//        2. 如果該票存在，且該票的擁有者是該買家，就開始退票
        if (ticket != null && ticket.getOwner().equals(buyerId)) {
//            2.1: 將退票過程加上鎖，確保在這個ticket被退票時，其他人不能退票這張票
            lock.lock();
            try {
                ticket.getIsSold().set(false);
                ticket.setOwner(null);
//                Signal all waiting threads that a ticket has become available.
//                這是用來通知其他等待的執行緒，有票可以買了
                ticketAvailable.signalAll();
                return "Ticket returned successfully";
            } finally {
                lock.unlock();
            }
        }
        return "Ticket return failed";
    }

    public void addTickets(AddTicketsInfo addTicketsInfo) {
        for (int i = 0; i < addTicketsInfo.getAddNum(); i++) {
            Ticket ticket = new Ticket();
            ticket.setId(String.valueOf(ticketId.incrementAndGet()));
            ticket.setType(addTicketsInfo.getType());
            ticket.setPrice(calculatePrice(addTicketsInfo.getType()));
            ticket.setIsSold(new AtomicBoolean(false));
            tickets.put(ticket.getId(), ticket);
        }
        System.out.println("We have " + tickets.size() + " tickets now");
    }

    public List<Ticket> getAllTickets() {
        return new ArrayList<>(tickets.values());
    }

    public List<Ticket> getBuyerTickets(String buyerId) {
        return tickets.values().stream()
                .filter(ticket -> buyerId.equals(ticket.getOwner()))
                .collect(Collectors.toList());
    }

    // check a buyer has bought 2 tickets or not
    public boolean checkBuyerHasBought2OrMoreTickets(String buyerId) {
        Integer count = buyerTickets.getOrDefault(buyerId, 0);
        return count >= TICKET_LIMIT;
    }

    // calculate price by ticket type
    private int calculatePrice(int type) {
        return type == 1 ? 1000 : 2000;
    }

}
