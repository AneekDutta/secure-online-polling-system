// Package declaration for main application entry
package com.polling;

// Import MainFrame user interface class
import com.polling.ui.MainFrame;
// Import SwingUtilities for thread-safe UI initialization on the Event Dispatch Thread
import javax.swing.SwingUtilities;
// Import UIManager for Look-and-Feel configuration
import javax.swing.UIManager;

// Main driver class launching the Secure Online Polling & Result Analysis System
public class Main {

    // Main entry point for the desktop application
    public static void main(String[] args) {
        // Print welcome header in console
        System.out.println("======================================================================");
        // Print application title
        System.out.println("  SECURE ONLINE POLLING & RESULT ANALYSIS SYSTEM (COLLEGE OOP PROJECT)");
        // Print author/course notice
        System.out.println("  Built with Java Swing, HashMap, and HashSet");
        // Print in-memory notice
        System.out.println("  Notice: Running in In-Memory Mode (Data resets when app is closed)");
        // Print separator
        System.out.println("======================================================================");

        // Configure system Look-and-Feel safely to match operating system desktop styles
        try {
            // Set platform-native look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        // Catch any look and feel exception without crashing
        } catch (Exception e) {
            // Print notice if fallback default is used
            System.out.println("Note: Default Look-and-Feel active (" + e.getMessage() + ")");
        // End of try-catch block
        }

        // Schedule desktop window creation on the Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            // Run method executed on the Event Dispatch Thread
            @Override
            public void run() {
                // Instantiate the main application window
                MainFrame frame = new MainFrame();
                // Make the desktop window visible to the user
                frame.setVisible(true);
            // End of run method
            }
        // End of Runnable
        });
    // End of main method
    }
// End of class
}
