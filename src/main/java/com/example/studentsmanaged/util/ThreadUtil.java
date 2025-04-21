package com.example.studentsmanaged.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class for thread management
 */
public class ThreadUtil {

    // Single thread executor for database operations
    private static final ExecutorService DB_EXECUTOR = Executors.newSingleThreadExecutor(
            new NamedThreadFactory("DB-Worker"));

    // Thread pool for file operations
    private static final ExecutorService FILE_EXECUTOR = Executors.newFixedThreadPool(2,
            new NamedThreadFactory("File-Worker"));

    // Thread pool for UI operations
    private static final ExecutorService UI_EXECUTOR = Executors.newFixedThreadPool(3,
            new NamedThreadFactory("UI-Worker"));

    /**
     * Get the executor service for database operations
     *
     * @return the database executor service
     */
    public static ExecutorService getDatabaseExecutor() {
        return DB_EXECUTOR;
    }

    /**
     * Get the executor service for file operations
     *
     * @return the file executor service
     */
    public static ExecutorService getFileExecutor() {
        return FILE_EXECUTOR;
    }

    /**
     * Get the executor service for UI operations
     *
     * @return the UI executor service
     */
    public static ExecutorService getUIExecutor() {
        return UI_EXECUTOR;
    }

    /**
     * Shutdown all executor services
     */
    public static void shutdownAll() {
        DB_EXECUTOR.shutdown();
        FILE_EXECUTOR.shutdown();
        UI_EXECUTOR.shutdown();

        // Shutdown logger
        LoggerUtil.shutdown();
    }

    /**
     * Run a task on the JavaFX application thread
     *
     * @param runnable the task to run
     */
    public static void runOnFXThread(Runnable runnable) {
        if (javafx.application.Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            javafx.application.Platform.runLater(runnable);
        }
    }

    /**
     * Custom thread factory that creates named threads
     */
    private static class NamedThreadFactory implements ThreadFactory {
        private final String namePrefix;
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        public NamedThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, namePrefix + "-" + threadNumber.getAndIncrement());

            // Make them daemon threads so they don't prevent JVM exit
            thread.setDaemon(true);

            // Set normal priority
            if (thread.getPriority() != Thread.NORM_PRIORITY) {
                thread.setPriority(Thread.NORM_PRIORITY);
            }

            return thread;
        }
    }
}