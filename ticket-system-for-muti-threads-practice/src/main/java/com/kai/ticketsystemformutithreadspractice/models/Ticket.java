package com.kai.ticketsystemformutithreadspractice.models;

import lombok.Data;

import java.util.concurrent.atomic.AtomicBoolean;

@Data
public class Ticket {

    private String id;

//    票種
    private int type;

    private int price;

//    在這邊為了確保ticket在被購買時不會被其他人購買，所以使用AtomicBoolean來確保ticket在被購買時不會被其他人購買。
    private AtomicBoolean isSold;

//    該票的擁有者
    private String owner;

}
