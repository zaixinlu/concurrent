
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Concurrent {

    Lock lock = new ReentrantLock();
    Condition condition = lock.newCondition();
    Semaphore semaphore = new Semaphore(3);

    public void lock() {
        lock.lock();
        try {
            System.out.println("locked");
            Thread.sleep(15_000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            System.out.println("unlocked");
        }
    }

    public void condition() throws InterruptedException {
        System.out.println("Get lock");
        lock.lock();
        System.out.println("Release lock for 10 seconds");
        System.out.println(LocalTime.now());
        condition.await(10, TimeUnit.SECONDS);
        System.out.println(LocalTime.now());
        System.out.println("Get lock again.");
    }

    public void executorService() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.submit(() -> {
            System.out.println("Task2 is running.");
            try {
                condition();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        executorService.submit(() -> {
            System.out.println("Task1 is running.");
            lock();
        });
        executorService.shutdown();
        executorService.awaitTermination(16, TimeUnit.SECONDS);
        System.out.println("is terminated? " + executorService.isTerminated());
    }

    public void semaphore() {
        try {
            semaphore.acquire();
            System.out.println(Thread.currentThread().getName() + " is working");
            Thread.sleep(10_000);
            semaphore.release();
            System.out.println(Thread.currentThread().getName() + " is over");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void executorServiceSemaphore() {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++)
            executorService.submit(() -> {
                semaphore();
            });
        executorService.shutdown();
    }

    public void blockingQueue() {
        Queue<Integer> blockingQueue = new ArrayBlockingQueue<>(20);
        Queue<Integer> queue = new ArrayDeque<>();
        blockingQueue.add(1);
        blockingQueue.add(2);
        blockingQueue.add(3);
        queue.offer(1);
        queue.offer(2);
        queue.offer(3);

        System.out.println(blockingQueue.peek());
        System.out.println(queue.peek());
    }

    public void concurrentHashMap() {
        Map<String, Integer> concurrentHashMap = new ConcurrentHashMap<>();
        concurrentHashMap.put("Hello", 1);
        concurrentHashMap.put("World", 2);

        System.out.println(concurrentHashMap.get("Hello"));
        System.out.println(concurrentHashMap.get("World"));
        System.out.println(concurrentHashMap.get("None"));
    }

    public void copyOnWriteList() {
        List<String> copyOnWriteList = new CopyOnWriteArrayList<>();
        copyOnWriteList.add("a");
        copyOnWriteList.add("b");
        copyOnWriteList.add("c");

        System.out.println(copyOnWriteList.size());
    }

    public static void main(String[] args) throws InterruptedException {
        Concurrent concurrent = new Concurrent();

        /*concurrent.concurrentHashMap();
        concurrent.blockingQueue();
        concurrent.copyOnWriteList();
        concurrent.executorService();*/
        concurrent.executorServiceSemaphore();
    }
}
