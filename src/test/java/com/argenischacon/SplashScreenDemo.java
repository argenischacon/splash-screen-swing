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

            // 2. Create an anonymous implementation of SplashWorker
            SplashWorker worker = new SplashWorker(splash) {
                @Override
                protected void performTasks() throws Exception {
                    // Simulate loading tasks with their respective messages and progress updates
                    updateProgress(10, "Connecting to server...");
                    Thread.sleep(500);

                    updateProgress(40, "Loading user settings...");
                    Thread.sleep(800);

                    updateProgress(75, "Initializing main modules...");
                    Thread.sleep(600);

                    updateProgress(90, "Preparing user interface...");
                    Thread.sleep(400);
                }

                @Override
                protected void onFinished() {
                    // 3. Launch the main application window after the splash screen closes.
                    JOptionPane.showMessageDialog(null, "Main Application Started", "Demo", JOptionPane.INFORMATION_MESSAGE);
                }
            };
            worker.start();
        });
    }
}