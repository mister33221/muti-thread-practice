package com.F32S.JUCbankpractice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.concurrent.RecursiveTask;


@AllArgsConstructor
@NoArgsConstructor
public class SumAmountTask extends RecursiveTask<Integer> {

    private List<Account> accounts;


    @Override
    protected Integer compute() {
//        1. 如果帳戶數量小於等於 10，則直接計算總金額
        if(accounts.size() <= 10){
            return accounts.stream().mapToInt(account -> account.getBalance().get()).sum();
        } else {
//        2. 如果帳戶數量大於 10，則分成兩半，分別計算總金額
//            subList 是用來取得 List 的子集合，第一個參數是起始索引，第二個參數是結束索引，但不包含結束索引的元素
            SumAmountTask left = new SumAmountTask(accounts.subList(0, accounts.size() / 2));
            SumAmountTask right = new SumAmountTask(accounts.subList(accounts.size() / 2, accounts.size()));
//            fork() 是用來執行子任務，join() 是用來取得子任務的結果
            left.fork();
            right.fork();
            return left.join() + right.join();
        }
    }
}
