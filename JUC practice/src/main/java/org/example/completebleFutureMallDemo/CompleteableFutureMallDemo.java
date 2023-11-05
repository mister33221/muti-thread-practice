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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class CompleteableFutureMallDemo {

    static List<NetMall> list = Arrays.asList(
            NetMall.builder().productName("jd").build(),
            NetMall.builder().productName("dangdang").build(),
            NetMall.builder().productName("taobao").build()
    );

    /**
     * step by step 一家家搜
     * @param productName
     * @return
     */
    public static List<String> getPriceByProductName(String productName) {
        return list.stream()
                .map(netMall -> netMall.calculatePrice(productName))
                .map(price -> "<<mySQL>> in " + productName + " price: " + price)
                .collect(java.util.stream.Collectors.toList());
    }

    public static void main(String[] args) {

        System.out.println(ThreadLocalRandom.current().nextDouble() * 2 + "mysql".charAt(0));

    }

}

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
class NetMall {
    private String productName;

    public double calculatePrice(String productName) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ThreadLocalRandom.current().nextDouble() * 2 + productName.charAt(0);

    }

}
