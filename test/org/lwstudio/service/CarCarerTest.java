package org.lwstudio.service;

import jdk.nashorn.internal.ir.annotations.Ignore;
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

    private final ExecutorService service = Executors.newSingleThreadExecutor();

    private final Car car = new Car();
    private final CarCarer washer = new CarCarer("Washing", car, CarStatus.WASHED, CarStatus.WAXED, CarStatus.CLEANED);

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
        car.setStatus(CarStatus.WASHED);

        startThread(washer);

        assertEquals(0, consoleOutput().length());

        endThreadAfterMilliseconds(200, washer);

        assertEquals(0, consoleOutput().length());
    }

    @Test
    public void testUpdateStatusAfterProcess() {
        car.setStatus(CarStatus.CLEANED);
        startThread(washer);

        assert(consoleOutput().contains("Washing"));

        endThreadAfterMilliseconds(1000, washer);

        assertEquals(CarStatus.WASHED, car.getStatus());
    }

    private void startThread(CarCarer carCarer) {
        service.execute(carCarer);
    }

    private void endThreadAfterMilliseconds(int milliseconds, CarCarer carCarer) {
        try {
            service.awaitTermination(milliseconds, TimeUnit.MILLISECONDS);
            carCarer.terminate();
            service.shutdownNow();
        } catch (InterruptedException e) {
            // ignore
        }
    }

    private String consoleOutput() {
        return systemOutContent.toString();
    }
}