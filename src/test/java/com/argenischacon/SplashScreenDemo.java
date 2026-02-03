package com.argenischacon;

import com.argenischacon.splash.SplashScreen;
import com.argenischacon.splash.SplashWorker;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class SplashScreenDemo {
    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            // 1. Instantiate the visual SplashScreen component
            SplashScreen splash = new SplashScreen("/icons/default.svg");

            // 2. Configure and build the SplashWorker using the fluent builder API
            SplashWorker worker = SplashWorker.create(splash)
                    .addTask("Connecting to server...", () -> Thread.sleep(500))
                    .addTask("Loading user settings...", () -> Thread.sleep(800))
                    .addTask("Initializing main modules...", () -> Thread.sleep(600))
                    .addTask("Preparing user interface...", () -> Thread.sleep(400))
                    //.addTask("Simulating Error", () -> { throw new RuntimeException("Database connection failed!"); })
                    .onError(e -> {
                        JOptionPane.showMessageDialog(null, "Initialization Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        System.exit(1); // Close app on critical error
                    })
                    .onFinished(() -> {
                        // 3. Launch the main application window after the splash screen closes.
                        JOptionPane.showMessageDialog(null, "Main Application Started", "Demo", JOptionPane.INFORMATION_MESSAGE);
                    })
                    .build();

            // 4. Start the worker
            worker.start();
        });
    }
}