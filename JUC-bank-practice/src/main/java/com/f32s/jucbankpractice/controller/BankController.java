package com.f32s.jucbankpractice.controller;

import com.f32s.jucbankpractice.exception.AbnormalTransactionException;
import com.f32s.jucbankpractice.model.Account;
import com.f32s.jucbankpractice.model.TransactionRecord;
import com.f32s.jucbankpractice.model.TransferInfo;
import com.f32s.jucbankpractice.service.BankService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

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
    @PostMapping("/transfer-kai")
    public String transfer(@RequestBody TransferInfo transferInfo) throws AbnormalTransactionException {
        return bankService.transfer(transferInfo);
    }


}
