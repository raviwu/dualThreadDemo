package org.lwstudio.service;

import org.lwstudio.entity.Car;
import org.lwstudio.entity.CarStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private void actionProcessing() throws InterruptedException {
        System.out.print(String.format("%1$-10s start ", action));

        for (int i = 0; i < CarCarer.ACTION_INTERVAL_COUNT; i++)  {
            Thread.sleep(CarCarer.ACTION_INTERVAL_MILLISECONDS);
            System.out.print(".");
        }

        Thread.sleep(CarCarer.ACTION_INTERVAL_MILLISECONDS);
        System.out.println(" finish!");
    }
}
