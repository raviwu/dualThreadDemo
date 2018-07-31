package org.lwstudio;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        final Car car = new Car();

        System.out.println("=== Program Started ===");

        executor.submit(new Runnable() {
            public void run() {
                try {
                    car.getWaxed();
                } catch (InterruptedException e) {
                    // e.printStackTrace();
                }
            }
        });
        executor.submit(new Runnable() {
            public void run() {
                try {
                    car.getWashed();
                } catch (InterruptedException e) {
                    // e.printStackTrace();
                }
            }
        });

        if (executor.awaitTermination(10, TimeUnit.SECONDS)) {
        } else {
            System.out.println();
            System.out.println("=== Program Finished ===");
            executor.shutdownNow();
        }
    }
}
