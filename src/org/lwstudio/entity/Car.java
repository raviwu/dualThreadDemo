package org.lwstudio.entity;

public class Car {
    public CarStatus status = CarStatus.CANWASH;

    synchronized public void getWashed() throws InterruptedException {
        while (true) {
            if (status != CarStatus.CANWASH) {
                wait();
            } else {
                processing("Washing");
                status = CarStatus.CANWAX;
                notifyAll();
            }
        }
    }

    synchronized public void getWaxed() throws InterruptedException {
        while (true) {
            if (status != CarStatus.CANWAX) {
                wait();
            } else {
                processing("Waxing");
                status = CarStatus.CANWASH;
                notifyAll();
            }
        }
    }

    private void processing(String action) throws InterruptedException {
        System.out.print(String.format("%1$-10s", action));

        for (int i = 0; i < 4; i++)  {
            Thread.currentThread().sleep(500);
            System.out.print(".");
        }

        System.out.println(" finish!");
    }
}

