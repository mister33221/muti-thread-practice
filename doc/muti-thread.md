# JUC-多線程

## 甚麼是多線程

多線程是指一個程序中包含了多個執行緒，這樣一來，每個執行緒都可以同時進行不同的任務執行緒，也就是說，多線程可以同時執行多個任務。
多線程與非同步在[這篇](https://ouch1978.github.io/blog/2022/09/25/understand-sync-async-and-multi-thread-with-one-pic)文章內有大概的說明，重點是非同步並不會增加執行緒。
![Alt text](image-3.png)

## 為什麼要使用多執行緒?

### 硬體

現在的電腦都是多核心的，如果只使用單執行緒，那麼其他的核心就會閒置，這樣就會造成資源的浪費。

### 軟體

如果一個程式只有單執行緒，那麼當程式遇到阻塞時，整個程式都會被阻塞，這樣就會造成資源的浪費。


### thread start 底層概述

- 編寫一段簡單的程式碼，並且執行，觀察結果
```java
public static void main(String[] args) {

        Thread t1 = new Thread(() ->{
            System.out.println("Hello from thread 1");
        });

        t1.start();

//        start會調用start0，並將started設為true，代表已經啟動
//        start0是個native method，表示是用C寫的底層方法，也就是說這個方法是在非Java代碼中實現的，通常是在C或C++中。這樣的方法被稱為本地方法。
//        本地方法允許Java與作業系統或硬體進行交互，或者訪問系統級別的資源。在Thread類別中，start0方法是用來啟動一個新的執行緒的，這個操作涉及到底層的系統調用，因此需要在本地代碼中實現。

    }
```
- jdk中有關thread的底層程式碼位置(以 openjdk8 為例)
  - `openjdk8/src/share/native/java/lang/Thread.c`
  - `openjdk8/src/share/vm/prims/jvm.cpp`
  - `openjdk8/src/share/vm/runtime/thread.cpp`
- java的線程是通過`start`的方法啟動執行，主要內容在native方法`start0`中
- openjdk寫的JNI(Java Native Interface)通常是1:1的，也就是說Thread.java對應的就是Thread.c
- 所以在查看JNINativeMethod的時候，可以在Thread.c中找到對應的方法，就是`start0`
    ![Alt text](image.png)
    ![Alt text](image-1.png)
    ![Alt text](image-2.png)

## 多線程相關概念

- 一把鎖
- 兩個併
  - 併發( Concurrent ):如同一時間，一堆人搶同一個票
    - 是在同一個實體上的多個事件(這裡實體是指CPU)
    - 是在一台處理器上，同一個時間間隔內，同時處理多個任務
    - 同一時刻，其實只有一個事件在發生
  - 併行( Parallel ):如同一時間有人去煮飯，有人去洗衣服
    - 是在不同實體上的多個事件
    - 是在多台處理器上，同一個時間間隔內，同時處理多個任務
    - 同一時刻，有多個事件在發生，你做你的，我做我的
- 三個程
  - 進程:一個程序的執行實例，如系統管理員看到我正在使用的程式就是進程，ex:廠房
  - 線程:一個進程中的執行單元，如一個進程中的多個執行緒，ex:廠房中的工人
  - 管程:一種同步工具，用於控制多個執行緒對共享資源的訪問
    - Monitor，也就是我們平常說的鎖，是一種同步工具，用於控制多個執行緒對共享資源的訪問
- 用戶線程與守護線程
  - 用戶線程( User Thread ):默認都是用戶線程，主要用於處理業務邏輯，當所有的用戶線程都結束時，JVM就會結束，如main方法
  - 守護線程( Daemon Thread ):主要用於處理用戶線程的輔助工作，當所有的用戶線程都結束時，JVM就會結束，如GC線程(垃圾回收線程)
  - 簡單範例，利用`setDaemon`方法設置為守護線程，觀察結果
  ```java
  // 判斷微 user thread or daemon thread
    public static void judgeDaemonThreadOrUserThread() {
        Thread t1 = new Thread(() -> {
            System.out.println("Hello from thread 1");
            while (true) {

            }
        });
    //        open this line, t1 will be daemon thread
    //        when main thread is done, t1 will be terminated

    //        comment this line, t1 will be user thread
    //        main thread is done, but t1 is still running, so the program is not terminated.
    //        t1.setDaemon(true);
            t1.start();
            
            System.out.println("main thread is done");
            
        }
  ```

## Future API

- Future API是一個介面，用於表示一個異步計算的結果，提供了方法來檢查計算是否完成，等待計算完成，檢索計算結果。可以為主線程開一個子線程，讓子線程去執行任務，主線程可以繼續執行其他任務，當需要子線程的結果時，再通過Future獲取結果。
- 簡單範例，利用`Future`來實現
    ```java
        public static void futureAPI() {
    //         ExecutorService 是一個介面，定義了一些方法，可以用來管理和執行執行緒
    //         Executors 是一個工具類，提供了一些靜態方法，用來創建不同類型的執行緒池
    //         newFixedThreadPool 創建一個固定大小的執行緒池，並且每次提交一個任務就創建一個執行緒，直到達到最大的執行緒數，這時候會將提交的任務存入到等待隊列中
            ExecutorService executorService = Executors.newFixedThreadPool(1);
    //        submit方法用來提交任務，並且返回一個Future，代表了將來要返回的結果
            Future<String> future = executorService.submit(() -> {
                Thread.sleep(5000);
                return "Hello from Callable, It will be returned after 5 seconds";
            });

            System.out.println("do something else");

    //        透過future.get()方法可以獲取結果，如果任務還沒有執行完，則會阻塞等待
            try {
                String result = future.get();
                System.out.println(result);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            executorService.shutdown();
        }
    ```

## Runable, Callable, Future, FutureTask example

- Runable是一個介面，只有一個run方法，用於定義一個任務，可以通過Thread或者ExecutorService來執行
- Callable是一個介面，只有一個call方法，用於定義一個任務，可以通過ExecutorService來執行，並且可以獲取結果
- Future是一個介面，定義了一些方法，用於獲取異步計算的結果
- FutureTask是一個類，實現了Future和Runnable，可以通過Thread或者ExecutorService來執行
  ![Alt text](image-4.png)
- 簡單範例
    ```java
    public static void runableCallableFutureFutureTask() throws ExecutionException, InterruptedException {
        // Runnable example
        // Runnable是一個接口，它代表一個要由線程執行的任務
        Runnable runnableTask = () -> {
            System.out.println("Runnable task is running");
        };
        new Thread(runnableTask).start(); // 創建一個新的線程來執行Runnable任務

        // Callable and Future example
        // Callable是一個接口，它代表一個會返回結果的任務
        Callable<String> callableTask = () -> {
            Thread.sleep(1000);
            return "Callable task's result";
        };
        ExecutorService executorService = Executors.newSingleThreadExecutor(); // 創建一個單線程的ExecutorService
        Future<String> future = executorService.submit(callableTask); // 提交Callable任務並獲得一個Future
        System.out.println("Future result: " + future.get()); // 使用Future的get方法來獲取任務的結果

        // FutureTask example
        // FutureTask是一種可以取消的異步計算，它實現了Runnable和Future接口
        FutureTask<String> futureTask = new FutureTask<>(callableTask); // 創建一個FutureTask
        new Thread(futureTask).start(); // 創建一個新的線程來執行FutureTask
        System.out.println("FutureTask result: " + futureTask.get()); // 使用FutureTask的get方法來獲取任務的結果

        executorService.shutdown(); // 關閉ExecutorService
    }   
    ```

## 比較有沒有使用多線程的差別

### 沒有使用多線程
```java
    public static void oneThread() {

//        三個任務，目前只有一個線程 main 來處理，耗時多久?

//        1. start
        long start = System.currentTimeMillis();

//        2. tasks
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(1000);
                System.out.println("Hello from thread " + i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

//        3. end
        long end = System.currentTimeMillis();

//        4. print result
        System.out.println("One thread 耗時: " + (end - start) + " ms");
    }
```
```console
Hello from thread 0
Hello from thread 1
Hello from thread 2
One thread 耗時: 3027 ms
```

### 使用多線程1

- 直接創建了三個新的線程來執行任務。這種方法更為直接，但是缺乏對線程生命週期的控制，並且不能獲取任務的結果。
```java
    public static void threeThreads() {

//        三個任務，用三個線程來處理，耗時多久?

//        1. start
        long start = System.currentTimeMillis();

//        2. tasks
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    System.out.println("Hello from thread " + Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

//        3. end
        long end = 0;
        while (Thread.activeCount() > 2) {
            end = System.currentTimeMillis();
        }

//        4. print result
        System.out.println("Three threads 耗時: " + (end - start) + " ms");
    }
```
```console
Hello from thread Thread-2
Hello from thread Thread-0
Hello from thread Thread-1
Three threads 耗時: 1007 ms
```

### 使用多線程2

- 使用了FutureTask和ExecutorService。FutureTask是一種可以取消的異步計算任務，它實現了Runnable和Future接口。ExecutorService是一種服務，它可以管理和控制線程的生命週期，包括創建、啟動、關閉線程等。在這段程式碼中，每個任務都被包裝成一個FutureTask，然後被提交到ExecutorService來執行。
```java
    public static void useFutureTaskForThreeThreads() {

//        三個任務，用三個線程來處理，耗時多久?

//        1. start
        long start = System.currentTimeMillis();

//        2. tasks
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        FutureTask<String> futureTask1 = new FutureTask<>(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("Hello from thread " + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Hello from thread " + Thread.currentThread().getName() + " is done";
        });
        executorService.submit(futureTask1);

        FutureTask<String> futureTask2 = new FutureTask<>(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("Hello from thread " + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Hello from thread " + Thread.currentThread().getName() + " is done";
        });
        executorService.submit(futureTask2);

        FutureTask<String> futureTask3 = new FutureTask<>(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("Hello from thread " + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Hello from thread " + Thread.currentThread().getName() + " is done";
        });
        executorService.submit(futureTask3);

        executorService.shutdown(); //ExecutorService的shutdown方法並不會立即終止執行中的任務，而是會禁止新的任務被提交到執行器服務。所有已經提交的任務都會被執行完畢。

//        3. end
        long end = 0;
        while (!futureTask1.isDone() || !futureTask2.isDone() || !futureTask3.isDone()) {
            end = System.currentTimeMillis();
        }
//        4. print result
        System.out.println("Three use FutureTask 耗時: " + (end - start) + " ms");

    }
```
```console
Hello from thread pool-1-thread-1
Hello from thread pool-1-thread-2
Hello from thread pool-1-thread-3
Three use FutureTask 耗時: 1022 ms
```

## Timeout

- 指定5秒，但我設訂的時間是3秒，所以會拋出TimeoutException
```java
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {

        timeoutTest(5);

    }

    public static void timeoutTest(int timeout) throws ExecutionException, InterruptedException, TimeoutException {

        FutureTask<String> futureTask = new FutureTask<>(() -> {
            System.out.println("Hello from thread " + Thread.currentThread().getName());
            // 將此線程暫停5秒
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Task done";
        });
        // the second parameter is the return value of Thread.currentThread().getName()
        Thread t1 = new Thread(futureTask, "t1");
        t1.start();

        System.out.println("main thread is done");

        System.out.println(futureTask.get(3, TimeUnit.SECONDS));

    }
```
```console
main thread is done
Hello from thread t1
Exception in thread "main" java.util.concurrent.TimeoutException
	at java.base/java.util.concurrent.FutureTask.get(FutureTask.java:204)
	at org.example.FutureTaskTimeout.timeoutTest(FutureTaskTimeout.java:33)
	at org.example.FutureTaskTimeout.main(FutureTaskTimeout.java:12)

```

## CompletableFuture

### 為什麼又多了一個CompletableFuture

- 在使用Future的時候遇到了幾個問題
  - Future的get方法是阻塞的，如果不調用get方法，則無法獲取結果
  - Future的get方法只能獲取結果，無法處理任務完成後的其他事情
  - Future的isDone方法只能使用輪巡的方式來判斷任務是否完成，這樣會浪費CPU資源

### CompletableFuture的接口

- CompletableFuture 是一個實現了 Future 和 CompletionStage  接口的類，它提供了非常豐富的方法來處理異步計算的結果。
- CompletionStage 中常用的方法
  - runAsync : 執行一個 Runnable 任務，無返回值
  - supplyAsync : 執行一個 Callable 任務，並且返回一個新的 CompletionStage
  - thenApply : 當前任務完成後，將結果作為參數傳遞給下一個任務
  - thenAccept : 當前任務完成後，將結果作為參數傳遞給下一個任務，但是不返回結果
  - thenRun : 當前任務完成後，執行下一個任務，但是不接受上一個任務的結果
  - thenCompose : 當前任務完成後，將結果作為參數傳遞給下一個任務，並且返回一個新的 CompletionStage
  - thenCombine : 當前任務完成後，將結果作為參數傳遞給下一個任務，並且返回一個新的 CompletionStage ，新的 CompletionStage 的結果是上一個任務的結果和下一個任務的結果的組合
- runAsync 範例
  ```java
    public static void useRunAsync() {

        System.out.println("useRunAsync");

        // runAsync use Runnable, so there is no return value
        // if you don't use specify the thread pool, it will use ForkJoinPool.commonPool()
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println("Hello from RunAsync thread who use" + Thread.currentThread().getName());
        });

        System.out.println("main thread is done");
        // join() is the same as get() but without checked exception
        future.join();
    }
  ```
  
- supplyAsync 範例
    ```java
    public static void useSupplyAsync() {

        System.out.println("useSupplyAsync");

        ExecutorService es = Executors.newFixedThreadPool(3);

        // supplyAsync use Supplier, so there is a return value
        // if you use specify the thread pool, it will a new thread pool
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("Hello from thread who use" + Thread.currentThread().getName());
            return "SupplyAsync task done";
        }, es);

        System.out.println("main thread is done");

        System.out.println(future.join());

        es.shutdown();

    }
    ```

## 參考資料
- [一張圖看懂同步、非同步與多執行緒的差別](https://ouch1978.github.io/blog/2022/09/25/understand-sync-async-and-multi-thread-with-one-pic)