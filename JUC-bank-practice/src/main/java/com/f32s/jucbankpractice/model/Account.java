package com.F32S.JUCbankpractice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.concurrent.atomic.AtomicInteger;

// 建立資料時，以 id = 1 為銀行帳號
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    // 帳號
    private String id;
    // 戶名
    private String name;
    // 餘額
    private AtomicInteger balance;
    // 轉帳次數
    private int transferTimes;

}
