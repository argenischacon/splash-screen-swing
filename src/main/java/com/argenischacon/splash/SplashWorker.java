package com.argenischacon.splash;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * An orchestrator for running background tasks while displaying a {@link SplashScreen}.
 * <p>
 * This class encapsulates the progress bar animation logic, ensures a minimum display time
 * for the splash screen, and provides a clear lifecycle for application initialization.
 * </p>
 * Use the {@link Builder} to configure and create an instance:
 * <pre>{@code
 * SplashWorker worker = SplashWorker.create(splash)
 *     .addTask("Loading modules...", () -> moduleLoader.load())
 *     .addTask("Connecting to database...", () -> db.connect())
 *     .onError(e -> JOptionPane.showMessageDialog(null, "Error: " + e.getMessage()))
 *     .onFinished(() -> new MainFrame().setVisible(true))
 *     .build();
 * worker.start();
 * }</pre>
 *
 * @see SplashScreen
 */
public final class SplashWorker extends SwingWorker<Void, Void> {

    private static final int MIN_SPLASH_TIME_MS = 2000;
    private final SplashScreen splash;
    private final long startTime;
    private final List<SplashTask> tasks;
    private final Runnable onFinishedCallback;
    private final Consumer<Throwable> onErrorCallback;

    private volatile int targetProgress = 0;
    private final Timer animationTimer;
    private volatile boolean backgroundWorkDone = false;
    private volatile Throwable executionError = null;

    /**
     * A functional interface for a task action that can throw an exception.
     */
    @FunctionalInterface
    public interface ThrowableRunnable {
        void run() throws Exception;
    }

    /**
     * Represents a single task to be executed during the splash screen.
     */
    private record SplashTask(String message, ThrowableRunnable action) {}

    private SplashWorker(SplashScreen splash, List<SplashTask> tasks, Runnable onFinishedCallback, Consumer<Throwable> onErrorCallback) {
        this.splash = Objects.requireNonNull(splash, "SplashScreen cannot be null");
        this.tasks = Objects.requireNonNull(tasks, "Task list cannot be null");
        this.onFinishedCallback = Objects.requireNonNull(onFinishedCallback, "onFinished callback cannot be null");
        this.onErrorCallback = Objects.requireNonNull(onErrorCallback, "onError callback cannot be null");
        this.startTime = System.currentTimeMillis();

        this.animationTimer = new Timer(15, e -> {
            if (splash.getProgress() < targetProgress) {
                splash.setProgress(splash.getProgress() + 1);
            }

            if (backgroundWorkDone) {
                // If an error occurred, stop immediately and handle it
                if (executionError != null) {
                    ((Timer) e.getSource()).stop();
                    splash.dispose();
                    onErrorCallback.accept(executionError);
                    return;
                }

                // Normal flow: wait for progress and min time
                if (splash.getProgress() >= 100) {
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    if (elapsedTime >= MIN_SPLASH_TIME_MS) {
                        ((Timer) e.getSource()).stop();
                        splash.dispose();
                        onFinished();
                    }
                }
            }
        });
    }

    /**
     * Starts the splash screen process. It makes the window visible and begins
     * executing the background tasks.
     */
    public void start() {
        splash.setVisible(true);
        animationTimer.start();
        execute();
    }

    @Override
    protected Void doInBackground() throws Exception {
        try {
            int totalTasks = tasks.size();
            if (totalTasks == 0) {
                return null;
            }

            for (int i = 0; i < totalTasks; i++) {
                SplashTask task = tasks.get(i);
                // Progress is based on completed tasks
                int progress = (int) ((i / (double) totalTasks) * 100);
                updateProgress(progress, task.message());
                task.action().run();
            }
        } finally {
            updateProgress(100, "Finalizando...");
        }
        return null;
    }

    @Override
    protected void done() {
        try {
            // Check if an exception occurred during doInBackground
            get();
        } catch (InterruptedException | ExecutionException e) {
            // Unwrap the exception to get the real cause
            this.executionError = e.getCause() != null ? e.getCause() : e;
        }
        this.backgroundWorkDone = true;
    }

    /**
     * Executes on the EDT after the splash screen is closed.
     */
    private void onFinished() {
        onFinishedCallback.run();
    }

    /**
     * Updates the splash screen's progress and status message from the background task.
     * This method is thread-safe.
     * @param progress The new progress value (0-100).
     * @param message The new status message to display.
     */
    private void updateProgress(int progress, String message) {
        SwingUtilities.invokeLater(() -> splash.setStatusText(message));
        this.targetProgress = Math.min(progress, 100);
    }

    /**
     * Creates a new {@link Builder} to configure a SplashWorker.
     *
     * @param splash The {@link SplashScreen} instance to control.
     * @return A new builder instance.
     */
    public static Builder create(SplashScreen splash) {
        return new Builder(splash);
    }

    /**
     * A builder for creating and configuring {@link SplashWorker} instances.
     */
    public static class Builder {
        private final SplashScreen splash;
        private final List<SplashTask> tasks = new ArrayList<>();
        private Runnable onFinishedCallback = () -> {}; // Default empty action
        private Consumer<Throwable> onErrorCallback = Throwable::printStackTrace; // Default print stack trace

        /**
         * Constructs a new builder.
         * @param splash The SplashScreen to be managed.
         */
        public Builder(SplashScreen splash) {
            this.splash = splash;
        }

        /**
         * Adds a task to be executed. Tasks are executed in the order they are added.
         *
         * @param message The message to display on the splash screen for this task.
         * @param action The long-running action to perform.
         * @return This builder instance for chaining.
         */
        public Builder addTask(String message, ThrowableRunnable action) {
            tasks.add(new SplashTask(message, action));
            return this;
        }

        /**
         * Sets the callback to be executed on the EDT after the splash screen closes.
         * This is typically used to launch the main application window.
         *
         * @param callback The action to run when finished.
         * @return This builder instance for chaining.
         */
        public Builder onFinished(Runnable callback) {
            this.onFinishedCallback = callback;
            return this;
        }

        /**
         * Sets the callback to be executed if an exception occurs during any task.
         *
         * @param callback The consumer to handle the exception.
         * @return This builder instance for chaining.
         */
        public Builder onError(Consumer<Throwable> callback) {
            this.onErrorCallback = callback;
            return this;
        }

        /**
         * Creates and returns a new {@link SplashWorker} with the configured settings.
         *
         * @return A new SplashWorker instance.
         */
        public SplashWorker build() {
            return new SplashWorker(splash, tasks, onFinishedCallback, onErrorCallback);
        }
    }
}
