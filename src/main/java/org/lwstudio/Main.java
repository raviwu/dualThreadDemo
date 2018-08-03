package org.lwstudio;

import org.lwstudio.service.CarCarer;
import org.lwstudio.entity.Car;
import org.lwstudio.entity.CarStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private final static int PROGRAM_EXECUTION_SECONDS = 5;
    private final static String PROGRAM_EXECUTION_COMPLETE_NOTICE = "=== Program Finished ==";
    private final static Car CAR = new Car();
    private static ExecutorService EXECUTOR;
    private static List<CarCarer> TASKS = new ArrayList<>();

    static {
        // TASKS.add(new CarCarer("Cleaning", CAR, Arrays.asList(CarStatus.WASHED, CarStatus.WAXED), CarStatus.CLEANED));
        TASKS.add(new CarCarer("Washing", CAR, Arrays.asList(CarStatus.WAXED, CarStatus.CLEANED), CarStatus.WASHED));
        TASKS.add(new CarCarer("Waxing", CAR, Arrays.asList(CarStatus.WASHED, CarStatus.CLEANED), CarStatus.WAXED));

        EXECUTOR = Executors.newFixedThreadPool(TASKS.size());
    }

    public static void main(String[] args) throws InterruptedException {
        TASKS.forEach((task) -> EXECUTOR.submit(task));

        EXECUTOR.awaitTermination(PROGRAM_EXECUTION_SECONDS, TimeUnit.SECONDS);

        TASKS.forEach(CarCarer::terminate);
        EXECUTOR.shutdown();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println(PROGRAM_EXECUTION_COMPLETE_NOTICE)));
    }
}