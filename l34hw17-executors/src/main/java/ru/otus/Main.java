package ru.otus;

import java.util.concurrent.Executors;

public class Main {
    private final Object monitor = new Object();
    private static final int THREADS_NUMBER = 1;
    private static final long INIT_THREAD_INDEX = 1L;
    private long currentThreadIndex = 1L;

    public static void main(String[] args) {
        var countdownInstance = new Main();
        var executorsPool = Executors.newFixedThreadPool(THREADS_NUMBER);

        for (int i = 0; i < THREADS_NUMBER; i++) {
            executorsPool.submit(() -> countdownInstance.startCountdown(1, 10));
        }
    }

    private void startCountdown(int from, int to) {
        synchronized (monitor) {
            final int ASC = 0;
            final int DESC = 1;
            int countdownSide = ASC;
            int currentValue = 0;

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    while ((Thread.currentThread().getId() % THREADS_NUMBER + 1L)  != currentThreadIndex) {
                        monitor.wait();
                    }

                    if (countdownSide == ASC && currentValue == to) {
                        countdownSide = DESC;
                    }
                    if (countdownSide == DESC && currentValue == from) {
                        countdownSide = ASC;
                    }

                    System.out.printf("Thread-%d: %d\n", currentThreadIndex, currentValue);
                    currentValue = (countdownSide == ASC) ? ++currentValue : --currentValue;
                    currentThreadIndex = (currentThreadIndex == THREADS_NUMBER) ? INIT_THREAD_INDEX : ++currentThreadIndex;
                    sleep();
                    monitor.notifyAll();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}