package com.example.studentsmanaged.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Utility class for logging application events
 */
public class LoggerUtil {
    // Log levels
    public enum LogLevel {
        DEBUG, INFO, WARNING, ERROR, SEVERE
    }

    // Singleton instance
    private static LoggerUtil instance;

    // Log file path
    private static final String LOG_DIRECTORY = "logs";
    private static final String LOG_FILE_PREFIX = "sms_log_";
    private static final String LOG_FILE_EXTENSION = ".log";

    // Current log file
    private File logFile;

    // Thread-safe message queue
    private final BlockingQueue<LogMessage> messageQueue;

    // Logger thread
    private Thread loggerThread;
    private volatile boolean running;

    // Date format for logs
    private final SimpleDateFormat dateFormat;
    private final SimpleDateFormat fileNameDateFormat;

    /**
     * Private constructor for singleton pattern
     */
    private LoggerUtil() {
        this.messageQueue = new LinkedBlockingQueue<>();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        this.fileNameDateFormat = new SimpleDateFormat("yyyyMMdd");

        // Create log directory if it doesn't exist
        File logDir = new File(LOG_DIRECTORY);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }

        // Create log file name with current date
        String fileName = LOG_FILE_PREFIX + fileNameDateFormat.format(new Date()) + LOG_FILE_EXTENSION;
        this.logFile = new File(logDir, fileName);

        // Start logger thread
        startLoggerThread();
    }

    /**
     * Get the singleton instance
     *
     * @return the logger instance
     */
    public static synchronized LoggerUtil getInstance() {
        if (instance == null) {
            instance = new LoggerUtil();
        }
        return instance;
    }

    /**
     * Start the logger thread
     */
    private void startLoggerThread() {
        running = true;
        loggerThread = new Thread(() -> {
            while (running) {
                try {
                    LogMessage message = messageQueue.take();
                    writeToFile(message);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    running = false;
                } catch (Exception e) {
                    System.err.println("Error in logger thread: " + e.getMessage());
                }
            }
        });

        loggerThread.setName("Logger-Thread");
        loggerThread.setDaemon(true);
        loggerThread.start();
    }

    /**
     * Write a log message to the file
     *
     * @param message the log message
     */
    private void writeToFile(LogMessage message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
            writer.println(formatLogMessage(message));
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }

    /**
     * Format a log message
     *
     * @param message the log message
     * @return the formatted message
     */
    private String formatLogMessage(LogMessage message) {
        return String.format("%s [%s] %s: %s",
                dateFormat.format(message.getTimestamp()),
                message.getLevel().name(),
                message.getSource(),
                message.getMessage());
    }

    /**
     * Log a debug message
     *
     * @param source the source of the log
     * @param message the log message
     */
    public static void debug(String source, String message) {
        log(LogLevel.DEBUG, source, message);
    }

    /**
     * Log an info message
     *
     * @param source the source of the log
     * @param message the log message
     */
    public static void info(String source, String message) {
        log(LogLevel.INFO, source, message);
    }

    /**
     * Log a warning message
     *
     * @param source the source of the log
     * @param message the log message
     */
    public static void warning(String source, String message) {
        log(LogLevel.WARNING, source, message);
    }

    /**
     * Log an error message
     *
     * @param source the source of the log
     * @param message the log message
     */
    public static void error(String source, String message) {
        log(LogLevel.ERROR, source, message);
    }

    /**
     * Log an error message with exception
     *
     * @param source the source of the log
     * @param message the log message
     * @param exception the exception
     */
    public static void error(String source, String message, Throwable exception) {
        StringBuilder sb = new StringBuilder(message);
        sb.append(" - Exception: ").append(exception.getMessage());
        sb.append("\nStack trace:\n");

        for (StackTraceElement element : exception.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }

        log(LogLevel.ERROR, source, sb.toString());
    }

    /**
     * Log a severe message
     *
     * @param source the source of the log
     * @param message the log message
     */
    public static void severe(String source, String message) {
        log(LogLevel.SEVERE, source, message);
    }

    /**
     * Log a message with a specific level
     *
     * @param level the log level
     * @param source the source of the log
     * @param message the log message
     */
    public static void log(LogLevel level, String source, String message) {
        getInstance().queueMessage(level, source, message);
    }

    /**
     * Queue a log message for writing
     *
     * @param level the log level
     * @param source the source of the log
     * @param message the log message
     */
    private void queueMessage(LogLevel level, String source, String message) {
        LogMessage logMessage = new LogMessage(level, source, message);
        messageQueue.offer(logMessage);
    }

    /**
     * Shutdown the logger
     */
    public static void shutdown() {
        LoggerUtil logger = getInstance();
        logger.running = false;
        if (logger.loggerThread != null) {
            logger.loggerThread.interrupt();
        }
    }

    /**
     * Inner class for log messages
     */
    private static class LogMessage {
        private final LogLevel level;
        private final String source;
        private final String message;
        private final Date timestamp;

        public LogMessage(LogLevel level, String source, String message) {
            this.level = level;
            this.source = source;
            this.message = message;
            this.timestamp = new Date();
        }

        public LogLevel getLevel() {
            return level;
        }

        public String getSource() {
            return source;
        }

        public String getMessage() {
            return message;
        }

        public Date getTimestamp() {
            return timestamp;
        }
    }
}