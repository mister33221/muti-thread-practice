package org.example.completeFuture;

public class StringComparisonExample {

    public static void main(String[] args) {
        // 創建三個變數，分別指向不同的字串物件
        String a = "car";
        String b = "car";
        String c = new String("car");

        // 檢查字串物件的記憶體地址
        System.out.println("a == b: " + (a == b)); // 預期為 true，因為它們參考相同的字串物件
        System.out.println("a == c: " + (a == c)); // 預期為 false，因為它們分別參考不同的字串物件

        // 檢查字串的內容是否相同
        System.out.println("a.equals(b): " + a.equals(b)); // 預期為 true，因為它們的內容相同
        System.out.println("a.equals(c): " + a.equals(c)); // 預期為 true，因為它們的內容相同

        // 檢查字串的記憶體地址是否相同
        System.out.println("a.hashCode() == b.hashCode(): " + (a.hashCode() == b.hashCode())); // 預期為 true
        System.out.println("a.hashCode() == c.hashCode(): " + (a.hashCode() == c.hashCode())); // 預期為 ?
    }

}
