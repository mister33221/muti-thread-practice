package com.kai.ticketsystemformutithreadspractice.models;

import lombok.Data;

import java.util.concurrent.atomic.AtomicBoolean;

@Data
public class Ticket {

    private String id;

    private int type;

    private int price;

    private AtomicBoolean isSold;

    private String owner;

}
