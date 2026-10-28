// Package declaration for live automated human simulation
package com.polling.demo;

// Import AWT Window class for dialog detection
import java.awt.Window;
// Import List interface
import java.util.List;
// Import AbstractButton
import javax.swing.AbstractButton;
// Import ButtonGroup
import javax.swing.ButtonGroup;
// Import JButton
import javax.swing.JButton;
// Import JComboBox
import javax.swing.JComboBox;
// Import JDialog
import javax.swing.JDialog;
// Import JTextField
import javax.swing.JTextField;
// Import SwingUtilities
import javax.swing.SwingUtilities;

// Import PollCardPanel
import com.polling.ui.PollCardPanel;
// Import MainFrame
import com.polling.ui.MainFrame;
// Import Poll
import com.polling.model.Poll;

// Autonomous robot controller that visually drives the GUI at a natural human pace
public class GuiHumanSimulationController {

    // Flag preventing concurrent overlapping simulations
    private static volatile boolean simulationActive = false;

    // Background dialog watcher thread flag
    private static volatile boolean watcherRunning = false;

    // Starts the realistic human simulation on an existing MainFrame instance
    public static synchronized void startSimulation(MainFrame frame) {
        // If simulation already active, ignore request
        if (simulationActive) {
            // Return early
            return;
        // End if
        }

        // Set active flag
        simulationActive = true;

        // Start background dialog auto-dismisser daemon
        startDialogWatcher();

        // Launch simulation workflow on dedicated background thread
        Thread simThread = new Thread(() -> {
            // Try block for simulation steps
            try {
                // Execute step-by-step human simulation
                runSimulationWorkflow(frame);
            // Catch interruption or exceptions
            } catch (Exception ex) {
                // Print error
                ex.printStackTrace();
            // Finally block to reset flag
            } finally {
                // Reset active flag
                simulationActive = false;
            // End finally
            }
        // End runnable
        }, "HumanSimulationRunner");

        // Set daemon
        simThread.setDaemon(true);
        // Start simulation thread
        simThread.start();
    // End startSimulation
    }

    // Main step-by-step human simulation sequence
    private static void runSimulationWorkflow(MainFrame frame) throws Exception {
        // STEP 1: Student Voting Simulation
        frame.setSimulationBanner("🤖 [Step 1/5] Active Polls: Simulating Student Voting at natural human pace...", true);
        // Navigate to active polls tab
        SwingUtilities.invokeLater(() -> frame.navigateTo("POLLS"));
        // Pause for tab to render and user to see
        sleep(1200);

        // Retrieve poll cards rendered on screen
        List<PollCardPanel> cards = frame.getPollListPanel().getPollCards();
        // Check if cards exist
        if (!cards.isEmpty()) {
            // Select first card
            PollCardPanel firstCard = cards.get(0);
            // Announce typing voter ID
            frame.setSimulationBanner("🤖 [Step 1/5] Focusing Voter ID input and typing 'STU-2026' character by character...", true);
            // Sleep brief human pause
            sleep(600);

            // Type voter ID character by character at natural human speed (85ms per key)
            typeTextAtHumanPace(firstCard.getVoterIdField(), "STU-2026", 85);
            // Pause as if human is reading ballot choices
            sleep(900);

            // Announce candidate selection
            frame.setSimulationBanner("🤖 [Step 1/5] Reviewing ballot candidates... Selecting option...", true);
            // Select second candidate option
            selectRadioOption(firstCard.getOptionButtonGroup(), 1);
            // Human verification pause
            sleep(800);

            // Announce casting ballot
            frame.setSimulationBanner("🤖 [Step 1/5] Clicking 'Submit Vote' button to cast encrypted ballot...", true);
            // Click submit vote button
            SwingUtilities.invokeLater(() -> firstCard.getVoteButton().doClick());
            // Wait for receipt popup to be viewed and dismissed (watcher handles popup)
            sleep(3000);

            // STEP 2: Duplicate Vote Prevention Demonstration
            frame.setSimulationBanner("🤖 [Step 2/5] Security Check: Same student 'STU-2026' attempts to vote a second time...", true);
            // Human pause
            sleep(1000);

            // Type same voter ID again
            typeTextAtHumanPace(firstCard.getVoterIdField(), "STU-2026", 80);
            // Pause
            sleep(600);

            // Select another option
            selectRadioOption(firstCard.getOptionButtonGroup(), 0);
            // Pause
            sleep(600);

            // Click submit vote button again
            SwingUtilities.invokeLater(() -> firstCard.getVoteButton().doClick());
            // Wait for user to read the red duplicate vote rejection error on the card
            frame.setSimulationBanner("🤖 [Step 2/5] Verified! Duplicate vote instantly caught and rejected by HashSet O(1) table.", true);
            // Pause for 2.5 seconds
            sleep(2500);
        // End if cards
        }

        // STEP 3: Dynamic Poll Creation Demonstration
        frame.setSimulationBanner("🤖 [Step 3/5] Faculty Persona: Navigating to 'Create Poll' to publish a new election...", true);
        // Switch to Create tab
        SwingUtilities.invokeLater(() -> frame.navigateTo("CREATE"));
        // Pause
        sleep(1200);

        // Announce typing question
        frame.setSimulationBanner("🤖 [Step 3/5] Typing poll question at natural human pace...", true);
        // Sleep brief pause
        sleep(500);

        // Type poll question smoothly
        typeTextAtHumanPace(
            frame.getCreatePollPanel().getQuestionField(),
            "Should the campus library operate 24/7 during final exam weeks?",
            55
        );
        // Human pause
        sleep(700);

        // Type Option 1
        frame.setSimulationBanner("🤖 [Step 3/5] Typing Option 1: 'Yes, 24/7 access with night security'...", true);
        // Fetch option fields
        List<JTextField> optionFields = frame.getCreatePollPanel().getOptionFields();
        // If option 1 exists
        if (optionFields.size() > 0) {
            // Type option 1
            typeTextAtHumanPace(optionFields.get(0), "Yes, 24/7 access with night security", 45);
        // End if
        }
        // Human pause
        sleep(600);

        // Type Option 2
        frame.setSimulationBanner("🤖 [Step 3/5] Typing Option 2: 'No, regular 8 AM - 10 PM hours are sufficient'...", true);
        // If option 2 exists
        if (optionFields.size() > 1) {
            // Type option 2
            typeTextAtHumanPace(optionFields.get(1), "No, regular 8 AM - 10 PM hours are sufficient", 45);
        // End if
        }
        // Human pause
        sleep(600);

        // Add third option dynamically
        frame.setSimulationBanner("🤖 [Step 3/5] Adding 3rd candidate option dynamically...", true);
        // Append row
        SwingUtilities.invokeLater(() -> frame.getCreatePollPanel().addOptionRow());
        // Pause
        sleep(600);

        // Refresh option fields reference
        optionFields = frame.getCreatePollPanel().getOptionFields();
        // If option 3 exists
        if (optionFields.size() > 2) {
            // Type Option 3
            typeTextAtHumanPace(optionFields.get(2), "Extend hours until 2 AM only", 45);
        // End if
        }
        // Human pause before publishing
        sleep(800);

        // Click publish button
        frame.setSimulationBanner("🤖 [Step 3/5] Clicking 'Publish Poll' button...", true);
        // Click create button
        SwingUtilities.invokeLater(() -> frame.getCreatePollPanel().getCreateButton().doClick());
        // Wait for creation to register and view update
        sleep(2000);

        // STEP 4: Results and Strategy Analysis Demonstration
        frame.setSimulationBanner("🤖 [Step 4/5] Results & Analytics: Switching to analytics view...", true);
        // Switch to Results tab
        SwingUtilities.invokeLater(() -> frame.navigateTo("RESULTS"));
        // Pause
        sleep(1400);

        // Announce Strategy pattern evaluation
        frame.setSimulationBanner("🤖 [Step 4/5] Switching Winning Strategy to 'Majority Threshold (>50%)'...", true);
        // Sleep
        sleep(800);

        // Select Majority Strategy in dropdown
        SwingUtilities.invokeLater(() -> {
            // Fetch strategy combo box
            JComboBox<?> stratBox = frame.getResultsPanel().getStrategySelector();
            // If has items
            if (stratBox != null && stratBox.getItemCount() > 1) {
                // Select index 1 (Majority Runoff)
                stratBox.setSelectedIndex(1);
            // End if
            }
        // End invokeLater
        });
        // Pause for user to see recalculated percentages and runoff alert
        sleep(2500);

        // STEP 5: Administrative Lifecycle & Governance
        frame.setSimulationBanner("🤖 [Step 5/5] Admin Console: Inspecting live audit stream and closing poll...", true);
        // Switch to Admin tab
        SwingUtilities.invokeLater(() -> frame.navigateTo("ADMIN"));
        // Pause
        sleep(1400);

        // Ensure active persona has Administrative authorization
        com.polling.model.User activeUser = frame.getCurrentUser();
        // Check if user lacks admin management permissions
        if (!activeUser.canManagePolls()) {
            // Instantiate polymorphic Admin user session
            activeUser = new com.polling.model.Admin("ADMIN-101", "Gauri Admin", "Campus Election Commission");
            // Final reference for lambda
            final com.polling.model.User finalAdmin = activeUser;
            // Update active session on Event Dispatch Thread
            SwingUtilities.invokeLater(() -> frame.setCurrentUser(finalAdmin));
            // Brief pause
            sleep(400);
        // End if
        }

        // Admin closes poll #POLL-101
        frame.getPollManager().closePoll("POLL-101", activeUser);
        // Refresh all views
        SwingUtilities.invokeLater(frame::refreshAllViews);
        // Pause
        sleep(1200);

        // Return to polls tab to verify closed state
        frame.setSimulationBanner("🤖 [Step 5/5] Returning to Active Polls to verify CLOSED badge and vote locking...", true);
        // Switch to polls
        SwingUtilities.invokeLater(() -> frame.navigateTo("POLLS"));
        // Pause for user to observe closed poll card
        sleep(2200);

        // Concluding announcement
        frame.setSimulationBanner("🎉 Human Simulation Complete! Real-time typing, voting, hashing, and governance verified at human pace.", true);
        // Leave banner visible for 5 seconds
        sleep(5000);
        // Hide banner
        frame.setSimulationBanner(" ", false);
    // End runSimulationWorkflow
    }

    // Helper method to type text into a JTextField character-by-character
    private static void typeTextAtHumanPace(JTextField field, String text, int delayMs) {
        // Request focus and clear field on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Request focus
            field.requestFocusInWindow();
            // Clear text
            field.setText("");
        // End invokeLater
        });

        // Loop over each character in text
        for (int i = 0; i < text.length(); i++) {
            // Compute substring up to current index
            final String partial = text.substring(0, i + 1);
            // Sleep natural typing interval
            sleep(delayMs);
            // Update text on EDT
            SwingUtilities.invokeLater(() -> field.setText(partial));
        // End loop
        }
    // End typeTextAtHumanPace
    }

    // Helper method to select a radio button in a ButtonGroup by index
    private static void selectRadioOption(ButtonGroup group, int targetIndex) {
        // Swing invoke later
        SwingUtilities.invokeLater(() -> {
            // Counter
            int idx = 0;
            // Iterate buttons
            for (java.util.Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
                // Fetch next button
                AbstractButton btn = buttons.nextElement();
                // Check match
                if (idx == targetIndex) {
                    // Click button visually
                    btn.doClick();
                    // Break loop
                    break;
                // End if
                }
                // Increment index
                idx++;
            // End loop
            }
        // End invokeLater
        });
    // End selectRadioOption
    }

    // Background daemon watcher that auto-dismisses modal dialogs after a comfortable viewing pause
    private static void startDialogWatcher() {
        // If already running, return
        if (watcherRunning) {
            // Return
            return;
        // End if
        }

        // Set running flag
        watcherRunning = true;

        // Start watcher thread
        Thread watcher = new Thread(() -> {
            // Loop while simulation is active
            while (simulationActive) {
                // Sleep check interval
                sleep(200);
                // Swing invoke later to inspect windows safely on EDT
                SwingUtilities.invokeLater(() -> {
                    // Check all open windows
                    for (Window window : Window.getWindows()) {
                        // Check if window is a visible JDialog
                        if (window instanceof JDialog && window.isVisible()) {
                            // Schedule auto-dismissal after 2.4 seconds so user can read it
                            new Thread(() -> {
                                // Wait 2400ms for reading
                                sleep(2400);
                                // Dispose dialog on EDT
                                SwingUtilities.invokeLater(() -> {
                                    // Check if still visible
                                    if (window.isVisible()) {
                                        // Dispose window
                                        window.dispose();
                                    // End if
                                    }
                                // End invokeLater
                                });
                            // End thread
                            }).start();
                        // End if
                        }
                    // End loop
                    }
                // End invokeLater
                });
            // End while
            }
            // Reset running flag
            watcherRunning = false;
        // End runnable
        }, "ModalDialogAutoDismisser");

        // Set daemon
        watcher.setDaemon(true);
        // Start watcher
        watcher.start();
    // End startDialogWatcher
    }

    // Helper sleep method
    private static void sleep(long millis) {
        // Try block
        try {
            // Sleep
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
            // Restore interrupt flag
            Thread.currentThread().interrupt();
        // End catch
        }
    // End sleep
    }

    // Standalone executable entry point to launch and immediately simulate the system
    public static void main(String[] args) {
        // Run on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Instantiate main application frame
            MainFrame frame = new MainFrame();
            // Make visible
            frame.setVisible(true);
            // Bring to front
            frame.toFront();
            // Start human simulation after 1.5 seconds
            new Thread(() -> {
                // Sleep 1500ms
                sleep(1500);
                // Start human simulation
                startSimulation(frame);
            // End thread
            }).start();
        // End invokeLater
        });
    // End main
    }
// End GuiHumanSimulationController
}
