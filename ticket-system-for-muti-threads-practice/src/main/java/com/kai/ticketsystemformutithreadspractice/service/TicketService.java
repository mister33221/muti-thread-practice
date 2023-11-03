package com.kai.ticketsystemformutithreadspractice.service;

import com.kai.ticketsystemformutithreadspractice.models.AddTicketsInfo;
import com.kai.ticketsystemformutithreadspractice.models.Ticket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class TicketService {

    private ConcurrentHashMap<String, Ticket> tickets = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> buyerTickets = new ConcurrentHashMap<>();
    private AtomicInteger ticketId = new AtomicInteger();
    private Lock lock = new ReentrantLock();
    private Condition ticketAvailable = lock.newCondition();

    public Object buyTicket(int type, String buyerId) throws InterruptedException {
        // version 1
        Integer count = buyerTickets.getOrDefault(buyerId, 0);
        if (count >= 2) {
            return "You can't buy more than 2 tickets";
        }

        for (Ticket ticket : tickets.values()) {
            synchronized (ticket) {
                if (!ticket.getIsSold().get() && ticket.getType() == type) {
                    ticket.getIsSold().set(true);
                    ticket.setOwner(buyerId);
                    buyerTickets.put(buyerId, count + 1);
                    return ticket;
                }
            }
        }
        return "No ticket available";
//        version 2
//        Integer count = buyerTickets.getOrDefault(buyerId, 0);
//        if (count >= 2) {
//            return "You can't buy more than 2 tickets";
//        }
//
//        lock.lock();
//        try {
//            while (true) {
//                for (Ticket ticket : tickets.values()) {
//                    if (!ticket.getIsSold().get() && ticket.getType() == type) {
//                        ticket.getIsSold().set(true);
//                        ticket.setOwner(buyerId);
//                        buyerTickets.put(buyerId, count + 1);
//                        return ticket;
//                    }
//                }
//                // If no ticket is available, wait until a ticket becomes available.
//                ticketAvailable.await();
//            }
//        } finally {
//            lock.unlock();
//        }
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
        Ticket ticket = tickets.get(ticketId);
        if (ticket != null && ticket.getOwner().equals(buyerId)) {
            lock.lock();
            try {
                ticket.getIsSold().set(false);
                ticket.setOwner(null);
                // Signal all waiting threads that a ticket has become available.
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
            ticket.setPrice(addTicketsInfo.getType() == 1 ? 1000 : 2000);
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

}
