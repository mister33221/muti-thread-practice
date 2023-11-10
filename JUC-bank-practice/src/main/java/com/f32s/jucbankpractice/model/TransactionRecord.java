package com.f32s.jucbankpractice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRecord {

    // 交易編號
    private String id;
    // 轉出帳戶
    private String fromAccount;

    // 轉入帳戶
    private String toAccount;

    // 轉帳結果
    private boolean result;

    // 交易時間
    private LocalDateTime time;

    // 交易金額
    private int amount;

    // 交易類型
    private String type;

}
