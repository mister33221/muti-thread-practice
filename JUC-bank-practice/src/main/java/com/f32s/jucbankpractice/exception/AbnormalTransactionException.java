package com.F32S.JUCbankpractice.exception;

import java.io.Serial;

public class AbnormalTransactionException extends Exception{
    @Serial
    private static final long serialVersionUID = 1L;
    public AbnormalTransactionException(String message){
        super(message);
    }
}
