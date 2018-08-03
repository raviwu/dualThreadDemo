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

    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    private final Car car = new Car();
    private final CarCarer washer = new CarCarer("Washing", car, CarStatus.WASHED, CarStatus.CLEANED);

    @BeforeEach
    void redirectSystemOutStream() {
        originalSystemOut = System.out;

        systemOutContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(systemOutContent));

        cleanupOutput();
    }

    @AfterEach
    void restoreSystemOutStream() {
        System.setOut(originalSystemOut);

        cleanupOutput();
    }

    @Test
    public void testTermination() throws InterruptedException {
        car.setStatus(CarStatus.CLEANED);

        startThread(washer);

        String initialOutput = consoleOutput();

        TimeUnit.MILLISECONDS.sleep(100);

        assert(consoleOutput().length() > initialOutput.length());

        endThreadAfterMilliseconds(200, washer);

        String finalOutput = consoleOutput();
        TimeUnit.MILLISECONDS.sleep(100);

        assertEquals(finalOutput, consoleOutput());
    }

    @Test
    public void testWaitForBeforeActionStatuses() throws InterruptedException {
        car.setStatus(CarStatus.WAXED);

        assertNoOutput();

        startThread(washer);

        assertNoOutput();

        endThreadAfterMilliseconds(1000, washer);

        assertNoOutput();
    }

    @Test
    public void testAllCarerWaitForCarStatus() {
        car.setStatus(CarStatus.WAXED);

        CarCarer cleaner = new CarCarer("Cleaning", car, CarStatus.CLEANED, CarStatus.WASHED);

        startThread(washer, cleaner);
        endThreadAfterMilliseconds(100, washer, cleaner);

        assertNoOutput();
    }

    @Test
    public void testCarCarerAcceptsManyCarStatuses() throws InterruptedException {
        car.setStatus(CarStatus.WAXED);

        CarCarer randomCarer = new CarCarer("Testing", car, CarStatus.CLEANED, CarStatus.WASHED, CarStatus.WAXED);

        startThread(randomCarer);
        endThreadAfterMilliseconds(100, randomCarer);

        assertEquals(true, consoleOutput().contains("Testing"));
    }

    @Test
    public void testUpdateStatusAfterProcess() {
        car.setStatus(CarStatus.CLEANED);

        startThread(washer);

        endThreadAfterMilliseconds(100, washer);

        assertEquals(true, consoleOutput().contains("Washing"));

        assertEquals(CarStatus.WASHED, car.getStatus());
    }

    private void assertNoOutput() {
        assertEquals("", consoleOutput());
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

    private void cleanupOutput() {
        systemOutContent.reset();
    }

    private String consoleOutput() {
        return systemOutContent.toString();
    }
}