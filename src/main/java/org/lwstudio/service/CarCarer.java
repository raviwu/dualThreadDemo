package org.lwstudio.service;

import org.lwstudio.entity.Car;
import org.lwstudio.entity.CarStatus;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class CarCarer implements Runnable {
    private final static int ACTION_INTERVAL_MILLISECONDS = 10;
    private final static int ACTION_INTERVAL_COUNT = 4;
    private boolean isRunning = true;
    private String action;

    private final Car car;

    private List<CarStatus> beforeActionStatuses;
    private CarStatus afterActionStatus;

    public CarCarer(String action, Car car, List<CarStatus> beforeActionStatuses, CarStatus afterActionStatus) {
        this.action = action;
        this.car = car;
        this.beforeActionStatuses = beforeActionStatuses;
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
            // e.printStackTrace();
        }
    }

    private void actionProcessing() {
        System.out.printf("%1$-10s start ", action);

        Stream.iterate(CarCarer.ACTION_INTERVAL_MILLISECONDS, i  ->  i)
                .limit(CarCarer.ACTION_INTERVAL_COUNT).forEach(CarCarer::actionProcessingEffect);

        System.out.println(" finish!");
    }

    private static void actionProcessingEffect(int sleepInterval) {
        try {
            TimeUnit.MILLISECONDS.sleep(sleepInterval);
            System.out.print(".");
        } catch (InterruptedException e) {
            // e.printStackTrace();
        }
    }
}