package com.F32S.JUCbankpractice.controller;


import com.F32S.JUCbankpractice.exception.AbnormalTransactionException;
import com.F32S.JUCbankpractice.model.Account;
import com.F32S.JUCbankpractice.model.TransactionRecord;
import com.F32S.JUCbankpractice.model.TransferInfo;
import com.F32S.JUCbankpractice.service.BankService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BankController {

    private BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @Operation(summary = "Generate bank accounts", tags = {"Init"})
    @GetMapping("/generateInitBankAccounts")
    public String generateInitBankAccounts() {
        return bankService.generateInitBankAccounts();
    }

    @Operation(summary = "Generate tracsaction records to diff db, except for the first one, use to simulation we have a lot of data need to be searched", tags = {"Init"})
    @GetMapping("/generateInitTransactionRecords")
    public String generateInitTransactionRecords() {
        return bankService.generateInitTransactionRecords();
    }

    @Operation(summary = "Get all accounts", tags = {"Init"})
    @GetMapping("/accounts")
    public List<Account> getAllAccounts() {
        return bankService.getAllAccounts();
    }

    @Operation(summary = "Get all transaction records", tags = {"Init"})
    @GetMapping("/transactionRecords")
    public List<TransactionRecord> getAllTransactionRecords() {
        return bankService.getAllTransactionRecords();
    }

    @Operation(summary = "Transfer money", tags = {"Transaction"})
    @PostMapping("/transfer")
    public String transfer(@RequestBody TransferInfo transferInfo) throws AbnormalTransactionException {
        return bankService.transfer(transferInfo);
    }


}
