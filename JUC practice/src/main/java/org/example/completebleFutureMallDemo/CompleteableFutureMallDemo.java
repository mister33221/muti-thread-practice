package org.example.completebleFutureMallDemo;


/*
1. 需求
    1.1 同一款產品，同時搜索出同款商品在各大店商平台的售價
    2.2 同一款產品，同時搜索出本產品在同一個店商平台下，各個入駐賣家的售價

2. 輸出: 出來的結果希望是同款產品在不同地方的嫁個顛列表，返回一個List<String>
    <<mySQL>> in jd price: 100
    <<mySQL>> in dangdang price: 200
    <<mySQL>> in taobao price: 300
 3. 技術需求
    3.1 functional programming
    3.2 chain pattern
    3.3 Stream流式計算
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
class NetMall {

    private String netMallName;

    public double calculatePrice(String productName) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ThreadLocalRandom.current().nextDouble() * 2 + productName.charAt(0);

    }

}

public class CompleteableFutureMallDemo {

    public static void main(String[] args) {

//        Demo 1: 一家家搜, 沒有使用多執行緒
        long startTime = System.currentTimeMillis();
        System.out.println(getPrice("mysql"));
        long endTime = System.currentTimeMillis();
        System.out.println("Did't use CompletableFuture: " + (endTime - startTime) + "ms");

//        Demo 2: 使用 completableFuture
//        note: 使用了 CompletableFuture，它是 Java 8 引入的一種更靈活的並行處理方式。
//              特點如下：
//              可以更靈活地控制並發處理，例如，您可以自訂線程池，設置超時，或者應對多個異步操作。
//              適合處理較複雜的並行任務，其中每個元素的處理可能需要不同的線程。
//              可以使用 join 方法等待所有 CompletableFuture 完成，以確保獲得最終結果。
//              缺點：
//              使用相對較多的程式碼，需要額外的 CompletableFuture 相關操作，可能不如 parallelStream 簡單。
//              需要更多的理解和掌握，特別是在處理較複雜的並行情況時。
        long startTime2 = System.currentTimeMillis();
        System.out.println(getPriceByCompletableFuture("mysql"));
        long endTime2 = System.currentTimeMillis();
        System.out.println("Use CompletableFuture: " + (endTime2 - startTime2) + "ms");

//        Demo 3: 使用 parallelStream
//        note: 使用了 Java 8 引入的 parallelStream() 方法，它將串行的流轉換為並行流，並利用多核處理器進行並行處理。
//              特點如下：
//              使用簡單，只需一行程式碼即可實現並行處理。
//              較適合用於對一個集合中的元素進行相對簡單的操作，如映射和過濾。
//              與串行流相比，相對容易理解和維護。
//              缺點：
//              不太適合處理複雜的並行任務，無法自訂線程數或控制並行處理的細節。
//              在某些情況下，可能會產生性能問題，因為它可能會過度並行，導致多線程競爭和上下文切換。
        long startTime3 = System.currentTimeMillis();
        System.out.println(getPriceByParallelStream("mysql"));
        long endTime3 = System.currentTimeMillis();
        System.out.println("Use parallelStream: " + (endTime3 - startTime3) + "ms");


//        如果您只需要簡單的並行處理，而且不需要太多細節控制，則 parallelStream 是一個簡單且有效的方法。
//        如果您需要更多控制，或者處理複雜的並行任務，那麼 CompletableFuture 提供了更大的靈活性。

    }

    static List<NetMall> list = Arrays.asList(
            NetMall.builder().netMallName("jd").build(),
            NetMall.builder().netMallName("dangdang").build(),
            NetMall.builder().netMallName("taobao").build(),
            NetMall.builder().netMallName("tmall").build(),
            NetMall.builder().netMallName("amazon").build(),
            NetMall.builder().netMallName("ebay").build(),
            NetMall.builder().netMallName("aliexpress").build(),
            NetMall.builder().netMallName("walmart").build(),
            NetMall.builder().netMallName("costco").build()
    );

    /**
     * step by step 一家家搜
     * @param productName
     * @return List<String>
     */
    public static List<String> getPrice(String productName) {
        return list.stream()
                .map(netMall -> productName + " in " + netMall.getNetMallName() + " price: " + String.format("%.2f", netMall.calculatePrice(productName)))
                .toList();
    }

    /**
     * 使用CompletableFuture，同時搜
     * @param productName
     * @return List<String>
     */
    public static List<String> getPriceByCompletableFuture(String productName) {
        return list.stream()
                .map(netMall -> CompletableFuture.supplyAsync(() -> netMall.getNetMallName() + " price: " + String.format("%.2f", netMall.calculatePrice(productName))))
                .toList()
                .stream()
                .map(CompletableFuture::join)
                .toList();
    }

    /**
     * 使用 parallelStream，同時搜
     * @param productName
     * @return List<String>
     */
    public static List<String> getPriceByParallelStream(String productName) {
        return list.parallelStream()
                .map(netMall -> netMall.getNetMallName() + " price: " + String.format("%.2f", netMall.calculatePrice(productName)))
                .toList();
    }

}

