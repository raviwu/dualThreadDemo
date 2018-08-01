package org.lwstudio.service;

import org.lwstudio.entity.Car;
import org.lwstudio.entity.CarStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CarCarer implements Runnable {
    private boolean isRunning = true;
    private String action;
    private final int actionIntervalMilliseconds = 500;

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
            synchronized (car) {
                while (isRunning) {
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

        for (int i = 0; i < 4; i++)  {
            Thread.sleep(actionIntervalMilliseconds);
            System.out.print(".");
        }

        Thread.sleep(actionIntervalMilliseconds);
        System.out.println(" finish!");
    }
}
