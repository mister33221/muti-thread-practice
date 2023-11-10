package com.f32s.jucbankpractice.model;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TransferInfo {

    @Schema(description = "轉出帳戶", example = "1")
    private String fromAccount;
    @Schema(description = "轉入帳戶", example = "2")
    private String toAccount;
    @Schema(description = "交易金額", example = "100")
    private int amount;

}
