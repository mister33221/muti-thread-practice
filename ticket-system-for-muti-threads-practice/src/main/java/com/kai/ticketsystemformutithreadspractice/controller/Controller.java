package com.kai.ticketsystemformutithreadspractice.controller;

import com.kai.ticketsystemformutithreadspractice.models.AddTicketsInfo;
import com.kai.ticketsystemformutithreadspractice.models.BuyTicketInfo;
import com.kai.ticketsystemformutithreadspractice.models.Ticket;
import com.kai.ticketsystemformutithreadspractice.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
public class Controller {

    @Autowired
    private TicketService ticketService;

    @Operation(summary = "(with muti threads)provide ticket type and buyer ID to buy a ticket")
    @PostMapping(path = "/buy1", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> buyTicket(@RequestBody BuyTicketInfo buyTicketInfo) throws InterruptedException {
        return ResponseEntity.ok(ticketService.buyTicket(buyTicketInfo.getType(), buyTicketInfo.getBuyerId()));
    }

//    @Operation(summary = "(without muti threads)provide ticket type and buyer ID to buy a ticket")
//    @PostMapping("/buy2")
//    public Ticket buyTicket2(@RequestParam @Parameter(description = "Ticket type", example = "1") int type,
//                             @RequestParam @Parameter(description = "Buyer ID", example = "1") String buyerId) {
//        // implementation
//        return null;
//    }

    @Operation(summary = "Provide buyer id and ticket id to return a ticket")
    @PostMapping("/return")
    public String returnTicket(@RequestParam @Parameter(description = "Buyer ID",example = "1") String buyerId,
                                @RequestParam @Parameter(description = "Ticket ID", example = "1") String ticketId) {
        return ticketService.returnTicket(buyerId, ticketId);
    }

    @Operation(summary = "Provide ticket type and number to add tickets")
    @PostMapping("/add")
    public void addTickets(@RequestBody AddTicketsInfo addTicketsInfo) {
        ticketService.addTickets(addTicketsInfo);
    }

    @Operation(summary = "get all the tickets to understand the current status")
    @GetMapping("/tickets")
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }

    @Operation(summary ="get all the tickets of a buyer")
    @GetMapping("/buyerTickets")
    public List<Ticket> getBuyerTickets(@RequestParam String buyerId) {
        return ticketService.getBuyerTickets(buyerId);
    }

    @Operation(summary = " test jmeter")
    @GetMapping("/test")
    public String  test() {
        System.out.println("test");
        return "test";
    }

}
