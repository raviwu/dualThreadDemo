package org.lwstudio.entity;

public class Car {
    private CarStatus status = CarStatus.CLEANED;

    synchronized public CarStatus getStatus() {
        return status;
    }

    synchronized public void setStatus(CarStatus status) {
        this.status = status;
    }
}

