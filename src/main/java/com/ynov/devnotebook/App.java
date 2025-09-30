package com.ynov.devnotebook;

import com.ynov.devnotebook.ui.MainFrame;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Application entry point: configures logging and starts the Swing UI on the EDT.
 */
public final class App {
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    private App() {}

    public static void main(String[] args) {
        configureLogging();
        LOGGER.info("Starting Dev Notebook application");
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                LOGGER.warning("Could not set system look and feel: " + e.getMessage());
            }
            new MainFrame().setVisible(true);
        });
    }

    private static void configureLogging() {
        try (InputStream is = App.class.getResourceAsStream("/logging.properties")) {
            if (is != null) {
                LogManager.getLogManager().readConfiguration(is);
            }
        } catch (IOException e) {
            System.err.println("Failed to load logging configuration: " + e.getMessage());
        }
    }
}
