package com.argenischacon.splash;

import javax.swing.*;

/**
 * An abstract orchestrator for running background tasks while displaying a {@link SplashScreen}.
 * <p>
 * This class encapsulates the progress bar animation logic, ensures a minimum display time
 * for the splash screen, and provides a clear lifecycle for application initialization.
 * </p>
 * <p>
 * To use it, create a subclass and implement the abstract {@link #performTasks()} method to define
 * the loading operations. You can optionally override {@link #onFinished()} to launch your
 * application's main window after the splash screen has closed.
 * </p>
 *
 * @see SplashScreen
 */
public abstract class SplashWorker extends SwingWorker<Void, Void> {

    private static final int MIN_SPLASH_TIME_MS = 2000;
    private final SplashScreen splash;
    private final long startTime;

    private volatile int targetProgress = 0;
    private final Timer animationTimer;
    private volatile boolean backgroundWorkDone = false;

    /**
     * Constructs a new worker associated with a SplashScreen.
     *
     * @param splash The {@link SplashScreen} instance to control.
     */
    public SplashWorker(SplashScreen splash) {
        this.splash = splash;
        this.startTime = System.currentTimeMillis();
        this.animationTimer = new Timer(15, e -> {
            if (splash.getProgress() < targetProgress) {
                splash.setProgress(splash.getProgress() + 1);
            }

            if (backgroundWorkDone && splash.getProgress() >= 100) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                if (elapsedTime >= MIN_SPLASH_TIME_MS) {
                    SplashWorker.this.animationTimer.stop();
                    splash.dispose();
                    onFinished();
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
    protected final Void doInBackground() throws Exception {
        try {
            performTasks();
        } finally {
            updateProgress(100, "Finalizando...");
        }
        return null;
    }

    @Override
    protected final void done() {
        this.backgroundWorkDone = true;
    }

    /**
     * Implement this method to define the sequence of long-running tasks.
     * Call {@link #updateProgress(int, String)} within this method to update the splash screen's state.
     *
     * @throws Exception if an error occurs during task execution.
     */
    protected abstract void performTasks() throws Exception;

    /**
     * A callback method that executes on the Event Dispatch Thread (EDT) after the splash screen is closed.
     * Override this method to launch your application's main window.
     */
    protected void onFinished() {
    }

    protected final void updateProgress(int progress, String message) {
        SwingUtilities.invokeLater(() -> splash.setStatusText(message));
        this.targetProgress = Math.min(progress, 100);
    }
}
