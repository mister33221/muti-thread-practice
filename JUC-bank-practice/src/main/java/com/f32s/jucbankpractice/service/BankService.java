package com.F32S.JUCbankpractice.service;

import com.F32S.JUCbankpractice.exception.AbnormalTransactionException;
import com.F32S.JUCbankpractice.model.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class BankService {
    private static final int FROM_ACCOUNT_TIME_LIMIT = 30;
    private static final int FROM_ACCOUNT_AMOUNT_LIMIT = 20000;
    private static final int FROM_ACCOUNT_TIMES_LIMIT = 5;

    //    CopyOnWriteArrayList: 這是一個線程安全的ArrayList，底層實現是一個Object[]，每次修改時都會複製一份新的Object[]，修改完後再將原本的Object[]指向新的Object[]。
    private List<Account> accounts = new CopyOnWriteArrayList<>();
    private List<TransactionRecord> transactionRecords = new CopyOnWriteArrayList<>();

    // 模擬撈出存在不同位置的大量 transaction records
    private List<TransactionRecord> transactionRecordsInDB1 = new CopyOnWriteArrayList<>();
    private List<TransactionRecord> transactionRecordsInDB2 = new CopyOnWriteArrayList<>();
    private List<TransactionRecord> transactionRecordsInDB3 = new CopyOnWriteArrayList<>();

    // ReentrantLock: 這是一個可重入的互斥鎖，底層實現是一個狀態變量和一個等待鎖的隊列，狀態變量表示鎖的狀態，等待鎖的隊列存放等待鎖的線程。
    // 使用 ReentrantLock 來實現帳戶鎖，每個帳戶都有一把鎖，當有線程要對帳戶進行操作時，先鎖住該帳戶，操作完後再解鎖。
    private final Map<String, ReentrantLock> accountLocks = new HashMap<>();

    public String generateInitBankAccounts( int numberOfAccounts,  int initBalance) {
        accounts.add(Account.builder().id("1").name("銀行帳號").balance(new AtomicInteger(1000000)).transferTimes(0).build());
        for (int i = 2; i <= numberOfAccounts; i++) {
            accounts.add(Account.builder().id(String.valueOf(i)).name("帳號" + i).balance(new AtomicInteger(initBalance)).transferTimes(0).build());
        }

        return "Generate init bank accounts successfully!";
    }

    public List<Account> getAllAccounts() {
        return accounts;
    }

    public String generateInitTransactionRecords() {

        ExecutorService executorService = Executors.newFixedThreadPool(3);

        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            for (int i = 0; i < 1000; i++) {
                TransactionRecord transactionRecord = TransactionRecord.builder()
                        .id(UUID.randomUUID().toString())
                        .fromAccount("1")
                        .toAccount("2")
                        .result(true)
                        .time(LocalDateTime.now())
                        .amount(100)
                        .type(RecordType.TRANSFER.getType())
                        .build();
                transactionRecordsInDB1.add(transactionRecord);
            }
        }, executorService);

        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            for (int i = 0; i < 1000; i++) {
                TransactionRecord transactionRecord = TransactionRecord.builder()
                        .id(UUID.randomUUID().toString())
                        .fromAccount("1")
                        .toAccount("2")
                        .result(true)
                        .time(LocalDateTime.now())
                        .amount(100)
                        .type(RecordType.TRANSFER.getType())
                        .build();
                transactionRecordsInDB2.add(transactionRecord);
            }
        }, executorService);

        CompletableFuture<Void> future3 = CompletableFuture.runAsync(() -> {
            for (int i = 0; i < 1000; i++) {
                TransactionRecord transactionRecord = TransactionRecord.builder()
                        .id(UUID.randomUUID().toString())
                        .fromAccount("1")
                        .toAccount("2")
                        .result(true)
                        .time(LocalDateTime.now())
                        .amount(100)
                        .type(RecordType.TRANSFER.getType())
                        .build();
                transactionRecordsInDB3.add(transactionRecord);
            }
        }, executorService);

        CompletableFuture.allOf(future1, future2, future3).join();

        executorService.shutdown();

        return "Generate init transaction records successfully!";
    }

    public List<TransactionRecord> getAllTransactionRecords() {

////        version 1
//        這段程式碼的效能問題可能來自於多次的 addAll 操作，每次操作都會導致內部數組的複製和擴展。如果你知道最終的列表大小，你可以在創建 ArrayList 時預先設定其容量，這樣可以減少內部數組的複製和擴展次數。
//        List<TransactionRecord> allTransactionRecords = new ArrayList<>();
//
//        allTransactionRecords.addAll(transactionRecords);
//        allTransactionRecords.addAll(transactionRecordsInDB1);
//        allTransactionRecords.addAll(transactionRecordsInDB2);
//        allTransactionRecords.addAll(transactionRecordsInDB3);
//
//        return allTransactionRecords;

        // version 2
        int totalSize = 0;
        ExecutorService executorService = Executors.newFixedThreadPool(4);


        CompletableFuture<List<TransactionRecord>> future = CompletableFuture.supplyAsync(() -> {
            return transactionRecords;
        }, executorService);
        CompletableFuture<List<TransactionRecord>> future1 = CompletableFuture.supplyAsync(() -> {
            return transactionRecordsInDB1;
        }, executorService);
        CompletableFuture<List<TransactionRecord>> future2 = CompletableFuture.supplyAsync(() -> {
            return transactionRecordsInDB2;
        }, executorService);
        CompletableFuture<List<TransactionRecord>> future3 = CompletableFuture.supplyAsync(() -> {
            return transactionRecordsInDB3;
        }, executorService);

        CompletableFuture.allOf(future, future1, future2, future3).join();

        try {
            totalSize = future.get().size() + future1.get().size() + future2.get().size() + future3.get().size();
        } catch (Exception e) {
            e.printStackTrace();
        }

        executorService.shutdown();

        List<TransactionRecord> allTransactionRecords = new ArrayList<>(totalSize);

        allTransactionRecords.addAll(transactionRecords);
        allTransactionRecords.addAll(transactionRecordsInDB1);
        allTransactionRecords.addAll(transactionRecordsInDB2);
        allTransactionRecords.addAll(transactionRecordsInDB3);

        return allTransactionRecords;

    }

    private void checkTransactionLegality(String fromAccountId, LocalDateTime tranStartTime, int amount) throws AbnormalTransactionException {
        LocalDateTime limitTime = tranStartTime.minusSeconds(FROM_ACCOUNT_TIME_LIMIT);
        List<TransactionRecord> recordsInLimitTime = this.transactionRecords.stream()
                .filter(transactionRecord ->
                        fromAccountId.equals(transactionRecord.getFromAccount()) &&
                                transactionRecord.getTime().isAfter(limitTime)
                ).toList();
        if (recordsInLimitTime.size() + 1 >= FROM_ACCOUNT_TIMES_LIMIT) {
            throw new AbnormalTransactionException(FROM_ACCOUNT_TIME_LIMIT + "秒內超過" + FROM_ACCOUNT_TIMES_LIMIT + "次");
        }
        TransactionRecord previousRecord = Collections.max(this.transactionRecords, Comparator.comparing(TransactionRecord::getTime));
        if ((amount + previousRecord.getAmount()) > FROM_ACCOUNT_AMOUNT_LIMIT) {
            throw new AbnormalTransactionException("近2次交易匯款金額超過" + FROM_ACCOUNT_AMOUNT_LIMIT + "元");
        }
        int totalAmountInLimitTime = recordsInLimitTime.stream().mapToInt(TransactionRecord::getAmount).sum() + amount;
        if (totalAmountInLimitTime > FROM_ACCOUNT_AMOUNT_LIMIT) {
            throw new AbnormalTransactionException(FROM_ACCOUNT_TIME_LIMIT + "秒內匯款金額超過" + FROM_ACCOUNT_AMOUNT_LIMIT + "元");
        }

    }

    public String transfer(TransferInfo transferInfo) throws AbnormalTransactionException {

////        version 1
////        0. 初始資料
//        String fromAccountId = transferInfo.getFromAccount();
//        String toAccountId = transferInfo.getToAccount();
//        int fee = 0;
//
////        1. 檢查交易合法性
//        try {
//            checkTransactionLegality(transferInfo.getFromAccount(), LocalDateTime.now(), transferInfo.getAmount());
//        } catch (AbnormalTransactionException e) {
//            throw e;
//        }
//
////        2. 檢查帳戶是否存在，如果存在就宣告出該帳戶
//        Account fromAccount = checkAccountExistAndGet(fromAccountId);
//        Account toAccount = checkAccountExistAndGet(toAccountId);
//        if(fromAccount == null || toAccount == null) {
//            throw new AbnormalTransactionException("帳戶不存在");
//        }
//
////        3. 檢查帳戶餘額是否足夠
//        if(fromAccount.getBalance() < transferInfo.getAmount()) {
//            throw new AbnormalTransactionException("餘額不足");
//        }
//
////        4. 確認轉帳次數，3次免費，超過的話，手續費為10元
//        if(fromAccount.getTransferTimes() >= 3) {
//            fee = 10;
//        }
//
////        5. 更新帳戶餘額
//        fromAccount.setBalance(fromAccount.getBalance() - transferInfo.getAmount() - fee);
//        toAccount.setBalance(toAccount.getBalance() + transferInfo.getAmount());
//        if(fee > 0){
//            Account bankAccount = checkAccountExistAndGet("1");
//            bankAccount.setBalance(bankAccount.getBalance() + fee);
//        }
//        fromAccount.setTransferTimes(fromAccount.getTransferTimes() + 1);
//
////        6. 新增交易紀錄
//        TransactionRecord transactionRecord = TransactionRecord.builder()
//                .id(UUID.randomUUID().toString())
//                .fromAccount(fromAccountId)
//                .toAccount(toAccountId)
//                .result(true)
//                .time(LocalDateTime.now())
//                .amount(transferInfo.getAmount())
//                .type(RecordType.TRANSFER.getType())
//                .build();
//        this.transactionRecords.add(transactionRecord);
//
////        7. 新增手續費交易紀錄
//        if(fee > 0) {
//            TransactionRecord feeTransactionRecord = TransactionRecord.builder()
//                    .id(UUID.randomUUID().toString())
//                    .fromAccount(fromAccountId)
//                    .toAccount("1")
//                    .result(true)
//                    .time(LocalDateTime.now())
//                    .amount(fee)
//                    .type(RecordType.FEE.getType())
//                    .build();
//            this.transactionRecords.add(feeTransactionRecord);
//        }


//        version 2
//        0. 初始資料
        String fromAccountId = transferInfo.getFromAccount();
        String toAccountId = transferInfo.getToAccount();
        int fee = 0;

//        1. 檢查交易合法性
        try {
            checkTransactionLegality(transferInfo.getFromAccount(), LocalDateTime.now(), transferInfo.getAmount());
        } catch (AbnormalTransactionException e) {
            throw e;
        }

//        2. 檢查帳戶是否存在，如果存在就宣告出該帳戶
        Account fromAccount = checkAccountExistAndGet(fromAccountId);
        if (fromAccount == null ) {
            throw new AbnormalTransactionException("轉出帳戶不存在");
        }
        Account toAccount = checkAccountExistAndGet(toAccountId);
        if (toAccount == null ) {
            throw new AbnormalTransactionException("轉入帳戶不存在");
        }

//        3. 檢查帳戶餘額是否足夠
        if (checkAccountBalanceIsEnough(fromAccount, transferInfo.getAmount())) {
            throw new AbnormalTransactionException("餘額不足");
        }

//        4. 確認轉帳次數，3次免費，超過的話，手續費為10元
        fee = setFee(fromAccount.getTransferTimes());

        ReentrantLock fromAccountLock = accountLocks.get(fromAccountId);
        ReentrantLock toAccountLock = accountLocks.get(toAccountId);
//        ReentrantLock bankAccountLock = accountLocks.get("1");

        try {

//          4.1 使用帳戶的ID進行排序，來決定先鎖住哪個帳戶，藉此避免死鎖，目前帳戶ID是可以轉成Integer的String，但就算是UUID之類的，仍可使用字符串的比較方法來決定先鎖住哪個帳戶
            if(Integer.parseInt(fromAccountId) > Integer.parseInt(toAccountId)){
                fromAccountLock = accountLocks.computeIfAbsent(fromAccountId, k -> new ReentrantLock());
                toAccountLock = accountLocks.computeIfAbsent(toAccountId, k -> new ReentrantLock());
            } else {
                toAccountLock = accountLocks.computeIfAbsent(toAccountId, k -> new ReentrantLock());
                fromAccountLock = accountLocks.computeIfAbsent(fromAccountId, k -> new ReentrantLock());
            }

 //        5. 更新帳戶餘額
            fromAccount.getBalance().set(fromAccount.getBalance().intValue() - transferInfo.getAmount() - fee);
            toAccount.getBalance().set(toAccount.getBalance().intValue() + transferInfo.getAmount());
            if (fee > 0) {
                Account bankAccount = checkAccountExistAndGet("1");
                bankAccount.getBalance().set(bankAccount.getBalance().intValue() + fee);
            }
            fromAccount.setTransferTimes(fromAccount.getTransferTimes() + 1);


//        6. 新增交易紀錄
            TransactionRecord transactionRecord = TransactionRecord.builder()
                    .id(UUID.randomUUID().toString())
                    .fromAccount(fromAccountId)
                    .toAccount(toAccountId)
                    .result(true)
                    .time(LocalDateTime.now())
                    .amount(transferInfo.getAmount())
                    .type(RecordType.TRANSFER.getType())
                    .build();
            this.transactionRecords.add(transactionRecord);

//        7. 新增手續費交易紀錄
            if (fee > 0) {
                TransactionRecord feeTransactionRecord = TransactionRecord.builder()
                        .id(UUID.randomUUID().toString())
                        .fromAccount(fromAccountId)
                        .toAccount("1")
                        .result(true)
                        .time(LocalDateTime.now())
                        .amount(fee)
                        .type(RecordType.FEE.getType())
                        .build();
                this.transactionRecords.add(feeTransactionRecord);
            }

        } catch (Exception e) {
            throw new AbnormalTransactionException("轉帳失敗");
        } finally {
            fromAccountLock.unlock();
            toAccountLock.unlock();
        }

        return "轉帳成功";

    }

    public Integer computeTotalAmount() {

//        version 1
//        I will use fork/join framework to compute the total amount of all accounts
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        return forkJoinPool.invoke(new SumAmountTask(accounts));



//        version 2
//        I use foreach to compare the performance with version 1
//        int totalAmount = 0;
//        for(Account account : accounts){
//            totalAmount += account.getBalance().intValue();
//        }
//        return totalAmount;

//        version 3
//        I use stream to compare the performance with version 1


//        return accounts.stream().mapToInt(account -> account.getBalance().intValue()).sum();

//        version 4
//        I use parallelStream to compare the performance with version 1
//        return accounts.parallelStream().mapToInt(account -> account.getBalance().intValue()).sum();
    }

    private Account checkAccountExistAndGet(String accountId) {
        return this.accounts.stream().filter(account -> accountId.equals(account.getId())).findFirst().orElse(null);
    }

    private boolean checkAccountBalanceIsEnough(Account account, int amount) {
        return account.getBalance().intValue() < amount;
    }

    private int setFee(int transferTimes) {
        int fee = 0;
        if (transferTimes >= 3) {
            fee = 10;
        }
        return fee;
    }
}
