package org.lwstudio.service;

import org.junit.jupiter.api.*;

import org.lwstudio.entity.Car;
import org.lwstudio.entity.CarStatus;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class CarCarerTest {
    private PrintStream originalSystemOut;
    private ByteArrayOutputStream systemOutContent;

    private final ExecutorService executor = Executors.newScheduledThreadPool(2);

    private final Car car = new Car();
    private final CarCarer washer = new CarCarer("Washing", car, CarStatus.WASHED, CarStatus.CLEANED);

    @BeforeEach
    void redirectSystemOutStream() {
        originalSystemOut = System.out;

        systemOutContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(systemOutContent));
    }

    @AfterEach
    void restoreSystemOutStream() {
        System.setOut(originalSystemOut);
    }

    @Test
    public void testTermination() throws InterruptedException {
        car.setStatus(CarStatus.CLEANED);

        startThread(washer);

        String initialOutput = consoleOutput();

        Thread.sleep(200);

        assert(consoleOutput().length() > initialOutput.length());

        endThreadAfterMilliseconds(500, washer);

        String finalOutput = consoleOutput();
        Thread.sleep(200);

        assertEquals(finalOutput, consoleOutput());
    }

    @Test
    public void testWaitForBeforeActionStatuses() {
        car.setStatus(CarStatus.WAXED);

        startThread(washer);

        assertEquals(0, consoleOutput().length());

        endThreadAfterMilliseconds(200, washer);

        assertEquals(0, consoleOutput().length());
    }

    @Test
    public void testAllCarerWaitForCarStatus() throws InterruptedException {
        car.setStatus(CarStatus.WAXED);

        CarCarer cleaner = new CarCarer("Cleaning", car, CarStatus.CLEANED, CarStatus.WASHED);

        startThread(washer, cleaner);
        endThreadAfterMilliseconds(3000, washer, cleaner);

        assertEquals(0, consoleOutput().length());
    }

    @Test
    public void testCarCarerAcceptsManyCarStatuses() throws InterruptedException {
        car.setStatus(CarStatus.WAXED);

        CarCarer randomCarer = new CarCarer("Testing", car, CarStatus.CLEANED, CarStatus.WASHED, CarStatus.WAXED);

        startThread(randomCarer);
        endThreadAfterMilliseconds(500, randomCarer);

        assert(consoleOutput().contains("Testing"));
    }

    @Test
    public void testUpdateStatusAfterProcess() {
        car.setStatus(CarStatus.CLEANED);

        startThread(washer);

        endThreadAfterMilliseconds(1000, washer);

        assert(consoleOutput().contains("Washing"));

        assertEquals(CarStatus.WASHED, car.getStatus());
    }

    @Test
    public void testDualThreadWaitAndNotifyEachOther() throws InterruptedException {
        car.setStatus(CarStatus.CLEANED);

        CarCarer cleaner = new CarCarer("Cleaning", car, CarStatus.CLEANED, CarStatus.WASHED);

        startThread(washer, cleaner);
        endThreadAfterMilliseconds(3000, washer, cleaner);

        assert(consoleOutput().contains(
                "Cleaning   start .... finish!\n" +
                        "Washing    start .... finish!\n" +
                        "Cleaning   start .... finish!\n" +
                        "Washing    start .... finish!"
        ));
    }

    private void startThread(CarCarer... carCarers) {
        for(CarCarer carCarer : carCarers) {
            executor.submit(carCarer);
        }
    }

    private void endThreadAfterMilliseconds(int milliseconds, CarCarer... carCarers) {
        try {
            executor.awaitTermination(milliseconds, TimeUnit.MILLISECONDS);
            for(CarCarer carCarer : carCarers) {
                carCarer.terminate();
            }
            executor.shutdownNow();
        } catch (InterruptedException e) {
            // ignore
        }
    }

    private String consoleOutput() {
        return systemOutContent.toString();
    }
}