package com.F32S.JUCbankpractice.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.concurrent.RecursiveTask;


@AllArgsConstructor
@NoArgsConstructor
//RecursiveTask: 是Java的Fork/Join框架中的一個抽象類別，用於表示一個可以產生結果的遞迴任務。
// 這個類別的主要方法是compute()，這個方法需要在子類別中實現，並且用於定義任務的遞迴邏輯和結果的計算方式。
public class SumAmountTask extends RecursiveTask<Integer> {

//     用來判斷是否要分割任務的門檻值
    private static final int THRESHOLD = 10;

//    要處理的資料
    private List<Account> accounts;


    @Override
    protected Integer compute() {
//        1. 如果帳戶數量小於等於 10，則直接計算總金額
        if(accounts.size() <= THRESHOLD){
            return accounts.stream().mapToInt(account -> account.getBalance().get()).sum();
        } else {
//        2. 如果帳戶數量大於 10，則分成兩半，分別計算總金額
//            subList 是用來切割 List 的方法，第一個參數是起始位置，第二個參數是結束位置
            SumAmountTask left = new SumAmountTask(accounts.subList(0, accounts.size() / 2));
            SumAmountTask right = new SumAmountTask(accounts.subList(accounts.size() / 2, accounts.size()));
//           使用fork()方法時，該任務會被安排在ForkJoinPool中執行，但調用fork()的線程不會等待該任務完成。相反，它會立即返回，並且可以繼續執行其他任務。
//           然後，你可以在稍後的時間點使用join()方法來等待該任務完成並獲取其結果。
            left.fork();
            right.fork();
            return left.join() + right.join();
        }
    }
}
