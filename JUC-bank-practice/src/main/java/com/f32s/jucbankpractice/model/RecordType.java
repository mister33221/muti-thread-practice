package com.f32s.jucbankpractice.model;

import lombok.Getter;

public enum RecordType {

    DEPOSIT("存款", 0),
    WITHDRAW("提款", 1),
    TRANSFER("轉帳", 2),
    FEE("手續費", 3);

    @Getter
    private final String type;
    private final int TypeCode;

    RecordType(String type, int TypeCode) {
        this.type = type;
        this.TypeCode = TypeCode;
    }


}
