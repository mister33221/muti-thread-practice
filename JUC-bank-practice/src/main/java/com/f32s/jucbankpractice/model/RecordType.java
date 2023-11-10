package com.F32S.JUCbankpractice.model;

import lombok.Getter;

public enum RecordType {

    TRANSFER("轉帳"),
    FEE("手續費");

    @Getter
    private final String type;

    RecordType(String type) {
        this.type = type;
    }


}
