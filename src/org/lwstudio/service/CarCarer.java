package org.lwstudio.service;

import org.lwstudio.entity.Car;
import org.lwstudio.entity.CarStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class CarCarer implements Runnable {
    private final static int ACTION_INTERVAL_MILLISECONDS = 100;
    private final static int ACTION_INTERVAL_COUNT = 5;
    private boolean isRunning = true;
    private String action;

    private final Car car;

    private List<CarStatus> beforeActionStatuses = new ArrayList<>();
    private CarStatus afterActionStatus;

    public CarCarer(String action, Car car, CarStatus afterActionStatus, CarStatus... beforeActionStatuses) {
        this.action = action;
        this.car = car;
        Collections.addAll(this.beforeActionStatuses, beforeActionStatuses);
        this.afterActionStatus = afterActionStatus;
    }

    public void terminate() {
        this.isRunning = false;
    }

    @Override
    public void run() {
        try {
            while (isRunning) {
                synchronized (car) {
                    if (beforeActionStatuses.contains(car.getStatus())) {
                        actionProcessing();
                        car.setStatus(afterActionStatus);
                        car.notifyAll();
                    } else {
                        car.wait();
                    }
                }
            }
        } catch (InterruptedException e) {
            // ignore
        }
    }

    private void actionProcessing() {
        System.out.print(String.format("%1$-10s start ", action));

        Stream.iterate(CarCarer.ACTION_INTERVAL_MILLISECONDS, i  ->  i)
                .limit(CarCarer.ACTION_INTERVAL_COUNT).forEach(CarCarer::actionProcessingEffect);

        System.out.println(" finish!");
    }

    private static void actionProcessingEffect(int sleepInterval) {
        try {
            Thread.sleep(sleepInterval);
            System.out.print(".");
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
