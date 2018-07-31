package org.lwstudio;

public class Car {
    private enum Status { CANWASH, CANWAX }
    public Status status = Status.CANWASH;

    synchronized public void getWashed() throws InterruptedException {
        while (true) {
            if (status != Status.CANWASH) {
                wait();
            } else {
                processing("Washing");
                status = Status.CANWAX;
                notifyAll();
            }
        }
    }

    synchronized public void getWaxed() throws InterruptedException {
        while (true) {
            if (status != Status.CANWAX) {
                wait();
            } else {
                processing("Waxing");
                status = Status.CANWASH;
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

