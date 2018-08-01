package org.lwstudio;

import org.lwstudio.entity.Car;
import org.lwstudio.entity.CarStatus;
import org.lwstudio.service.CarCarer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private final static int programExecutionPeriodSeconds = 10;
    private final static Car CAR = new Car();
    private static ExecutorService EXECUTOR;
    private static List<CarCarer> TASKS = new ArrayList<>();

    static {
        // TASKS.add(new CarCarer("Cleaning", CAR, CarStatus.CLEANED, CarStatus.WASHED, CarStatus.WAXED));
        TASKS.add(new CarCarer("Washing", CAR, CarStatus.WASHED, CarStatus.WAXED, CarStatus.CLEANED));
        TASKS.add(new CarCarer("Waxing", CAR, CarStatus.WAXED, CarStatus.WASHED, CarStatus.CLEANED));

        EXECUTOR = Executors.newScheduledThreadPool(TASKS.size());
    }

    public static void main(String[] args) throws InterruptedException {
        TASKS.forEach((task) -> EXECUTOR.submit(task));

        EXECUTOR.awaitTermination(programExecutionPeriodSeconds, TimeUnit.SECONDS);

        TASKS.forEach(CarCarer::terminate);
        EXECUTOR.shutdown();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("=== Program Finished ==")));
    }
}
