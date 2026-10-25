// Package declaration for user interface components
package com.polling.ui;

// Import PollEvent for Observer Pattern
import com.polling.event.PollEvent;
// Import PollEventListener interface
import com.polling.event.PollEventListener;
// Import domain models
import com.polling.model.Admin;
// Import Poll
import com.polling.model.Poll;
// Import User
import com.polling.model.User;
// Import VoteReceipt
import com.polling.model.VoteReceipt;
// Import Voter
import com.polling.model.Voter;
// Import PollManager service
import com.polling.service.PollManager;
// Import PollResultSummary
import com.polling.service.PollResultSummary;
// Import ReportExporter service
import com.polling.service.ReportExporter;
// Import ResultAnalyzer service
import com.polling.service.ResultAnalyzer;
// Import AWT layout components
import java.awt.BorderLayout;
// Import Color
import java.awt.Color;
// Import Dimension
import java.awt.Dimension;
// Import FlowLayout
import java.awt.FlowLayout;
// Import GridBagConstraints
import java.awt.GridBagConstraints;
// Import GridBagLayout
import java.awt.GridBagLayout;
// Import Insets
import java.awt.Insets;
// Import File
import java.io.File;
// Import List
import java.util.List;
// Import Set
import java.util.Set;
// Import Swing components
import javax.swing.BorderFactory;
// Import Box
import javax.swing.Box;
// Import BoxLayout
import javax.swing.BoxLayout;
// Import JButton
import javax.swing.JButton;
// Import JFileChooser
import javax.swing.JFileChooser;
// Import JLabel
import javax.swing.JLabel;
// Import JOptionPane
import javax.swing.JOptionPane;
// Import JPanel
import javax.swing.JPanel;
// Import JScrollPane
import javax.swing.JScrollPane;
// Import JTable
import javax.swing.JTable;
// Import SwingUtilities
import javax.swing.SwingUtilities;

// Visual panel providing administrative controls, role switching (OOP Polymorphism), Observer auto-updates, and audit receipts
public class AdminPanel extends JPanel implements PollEventListener {

    // Serial version UID
    private static final long serialVersionUID = 1L;

    // Injected PollManager reference
    private final PollManager pollManager;

    // Supplier of active user
    private final java.util.function.Supplier<User> currentUserSupplier;

    // Consumer to update active user
    private final java.util.function.Consumer<User> userUpdateConsumer;

    // Refresh callback
    private final Runnable globalRefreshCallback;

    // Result analyzer for generating audit reports
    private final ResultAnalyzer resultAnalyzer;

    // Container holding admin poll table/cards
    private final JPanel adminContentPanel;

    // Label showing active role description (polymorphic)
    private final JLabel roleDescriptionLabel;

    // Label showing active role title
    private final JLabel roleTitleLabel;

    // Constructor assembling the administration panel
    public AdminPanel(PollManager pollManager,
                      java.util.function.Supplier<User> currentUserSupplier,
                      java.util.function.Consumer<User> userUpdateConsumer,
                      Runnable globalRefreshCallback) {
        // Call superclass constructor
        super();
        // Assign poll manager
        this.pollManager = pollManager;
        // Assign user supplier
        this.currentUserSupplier = currentUserSupplier;
        // Assign user updater
        this.userUpdateConsumer = userUpdateConsumer;
        // Assign global refresh callback
        this.globalRefreshCallback = globalRefreshCallback;
        // Initialize result analyzer
        this.resultAnalyzer = new ResultAnalyzer();

        // Set layout to BorderLayout
        setLayout(new BorderLayout(0, 16));
        // Set background color
        setBackground(ModernTheme.COLOR_BG_APP);
        // Set margin padding
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Create labels for polymorphic user status
        this.roleTitleLabel = new JLabel();
        // Role description
        this.roleDescriptionLabel = new JLabel();

        // Assemble top section with user persona switcher
        add(createTopSection(), BorderLayout.NORTH);

        // Content panel for administrative poll controls
        this.adminContentPanel = new JPanel();
        // Vertical layout
        this.adminContentPanel.setLayout(new BoxLayout(this.adminContentPanel, BoxLayout.Y_AXIS));
        // Background color
        this.adminContentPanel.setBackground(ModernTheme.COLOR_BG_APP);

        // Wrap inside JScrollPane
        JScrollPane scrollPane = new JScrollPane(adminContentPanel);
        // Remove border
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        // Background color
        scrollPane.setBackground(ModernTheme.COLOR_BG_APP);
        // Viewport background
        scrollPane.getViewport().setBackground(ModernTheme.COLOR_BG_APP);
        // Scroll speed
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Add scroll pane to center
        add(scrollPane, BorderLayout.CENTER);

        // Render initial data
        refreshAdminView();
    // End of constructor
    }

    // Observer callback automatically re-rendering admin controls when poll events fire
    @Override
    public void onPollEvent(PollEvent event) {
        // Dispatch UI update onto the Swing Event Dispatch Thread
        SwingUtilities.invokeLater(this::refreshAdminView);
    // End of method
    }

    // Creates the top section with persona switcher demonstrating OOP Polymorphism
    private JPanel createTopSection() {
        // Container panel
        JPanel topContainer = new JPanel();
        // Vertical layout
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        // Transparent
        topContainer.setOpaque(false);

        // Header title
        JLabel titleLabel = new JLabel("Administration & Security Console (Iteration 2)");
        // Heading font
        titleLabel.setFont(ModernTheme.FONT_HEADING);
        // Navy color
        titleLabel.setForeground(ModernTheme.COLOR_TEXT_PRIMARY);
        // Align left
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        // Add title
        topContainer.add(titleLabel);

        // Subtitle
        JLabel subLabel = new JLabel("Manage poll lifecycle, verify ballot receipts, audit SHA-256 voter hashes, and test OOP polymorphism.");
        // Body font
        subLabel.setFont(ModernTheme.FONT_BODY);
        // Muted color
        subLabel.setForeground(ModernTheme.COLOR_TEXT_MUTED);
        // Align left
        subLabel.setAlignmentX(LEFT_ALIGNMENT);
        // Add spacing
        topContainer.add(Box.createRigidArea(new Dimension(0, 4)));
        // Add subtitle
        topContainer.add(subLabel);
        // Spacing
        topContainer.add(Box.createRigidArea(new Dimension(0, 16)));

        // User Persona Card (demonstrates Abstraction, Inheritance, and Polymorphism)
        JPanel personaCard = new JPanel(new BorderLayout(16, 8));
        // White background
        personaCard.setBackground(ModernTheme.COLOR_BG_CARD);
        // Card border
        personaCard.setBorder(ModernTheme.createCardBorder());
        // Align left
        personaCard.setAlignmentX(LEFT_ALIGNMENT);

        // Left info panel
        JPanel infoPanel = new JPanel();
        // Vertical layout
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        // Transparent
        infoPanel.setOpaque(false);

        // Title styling
        roleTitleLabel.setFont(ModernTheme.FONT_SUBHEADING);
        // Color
        roleTitleLabel.setForeground(ModernTheme.COLOR_TEXT_PRIMARY);
        // Add title label
        infoPanel.add(roleTitleLabel);

        // Spacing
        infoPanel.add(Box.createRigidArea(new Dimension(0, 4)));

        // Description styling
        roleDescriptionLabel.setFont(ModernTheme.FONT_BODY);
        // Color
        roleDescriptionLabel.setForeground(ModernTheme.COLOR_TEXT_SECONDARY);
        // Add description label
        infoPanel.add(roleDescriptionLabel);

        // Add info panel to west
        personaCard.add(infoPanel, BorderLayout.WEST);

        // Right button switcher panel
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        // Transparent
        switchPanel.setOpaque(false);

        // Switch to Admin button
        JButton setAdminBtn = new JButton("Act as Administrator");
        // Style primary
        ModernTheme.stylePrimaryButton(setAdminBtn);
        // Click listener
        setAdminBtn.addActionListener(e -> {
            // Instantiate polymorphic Admin user
            User admin = new Admin("ADMIN-101", "Gauri Admin", "Campus Election Commission");
            // Update active user
            userUpdateConsumer.accept(admin);
            // Refresh admin view
            refreshAdminView();
            // Trigger global refresh
            globalRefreshCallback.run();
        // End listener
        });
        // Add to switch panel
        switchPanel.add(setAdminBtn);

        // Switch to Voter button
        JButton setVoterBtn = new JButton("Act as Standard Voter");
        // Style secondary
        ModernTheme.styleSecondaryButton(setVoterBtn);
        // Click listener
        setVoterBtn.addActionListener(e -> {
            // Instantiate polymorphic Voter user
            User voter = new Voter("VOTER-SAMPLE", "Aneek Regular Voter");
            // Update active user
            userUpdateConsumer.accept(voter);
            // Refresh admin view
            refreshAdminView();
            // Trigger global refresh
            globalRefreshCallback.run();
        // End listener
        });
        // Add to switch panel
        switchPanel.add(setVoterBtn);

        // Add switch panel to east
        personaCard.add(switchPanel, BorderLayout.EAST);

        // Add persona card to top container
        topContainer.add(personaCard);
        // Spacing
        topContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        // Return top container
        return topContainer;
    // End of method
    }

    // Refreshes the display of user details and poll management cards
    public void refreshAdminView() {
        // Retrieve current polymorphic user
        User user = currentUserSupplier.get();
        // Update role title label polymorphically
        roleTitleLabel.setText("Current Active Persona: " + user.getRole() + " (" + user.getName() + ")");
        // Update role description polymorphically
        roleDescriptionLabel.setText("Permissions: " + user.getPermissionsDescription());

        // Clear existing admin content
        adminContentPanel.removeAll();

        // Retrieve all polls
        List<Poll> polls = pollManager.getAllPolls();

        // Iterate through each poll
        for (Poll poll : polls) {
            // Card panel for this poll's administrative controls
            JPanel card = new JPanel(new BorderLayout(12, 12));
            // White card background
            card.setBackground(ModernTheme.COLOR_BG_CARD);
            // Card border
            card.setBorder(ModernTheme.createCardBorder());
            // Align left
            card.setAlignmentX(LEFT_ALIGNMENT);

            // Left details section
            JPanel detailsPanel = new JPanel();
            // Vertical layout
            detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
            // Transparent
            detailsPanel.setOpaque(false);

            // Poll title
            JLabel pollTitle = new JLabel(poll.getPollId() + ": " + poll.getQuestion());
            // Font
            pollTitle.setFont(ModernTheme.FONT_CARD_TITLE);
            // Color
            pollTitle.setForeground(ModernTheme.COLOR_TEXT_PRIMARY);
            // Add title
            detailsPanel.add(pollTitle);
            // Spacing
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 4)));

            // Metrics label
            JLabel metricsLabel = new JLabel("Status: " + (poll.isOpen() ? "OPEN" : "CLOSED") + "  |  Total Votes: " + poll.getTotalVotes() + "  |  SHA-256 Voter Hashes: " + poll.getVoterCount());
            // Font
            metricsLabel.setFont(ModernTheme.FONT_BODY);
            // Color
            metricsLabel.setForeground(ModernTheme.COLOR_TEXT_MUTED);
            // Add metrics
            detailsPanel.add(metricsLabel);

            // Add details panel to west
            card.add(detailsPanel, BorderLayout.WEST);

            // Right action buttons panel
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            // Transparent
            actionPanel.setOpaque(false);

            // Close / Reopen button
            String btnText = poll.isOpen() ? "Close Poll" : "Reopen Poll";
            // Create button
            JButton toggleBtn = new JButton(btnText);
            // Style secondary
            ModernTheme.styleSecondaryButton(toggleBtn);
            // Disable if user is not authorized (enforces OOP permission model in UI)
            toggleBtn.setEnabled(user.canManagePolls());
            // Tooltip if disabled
            if (!user.canManagePolls()) {
                // Set tooltip
                toggleBtn.setToolTipText("Only users with Admin role can close/reopen polls.");
            // End if
            }
            // Click listener
            toggleBtn.addActionListener(e -> {
                // Confirm action
                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to " + (poll.isOpen() ? "CLOSE" : "REOPEN") + " " + poll.getPollId() + "?",
                    "Admin Confirmation",
                    JOptionPane.YES_NO_OPTION
                );
                // If confirmed
                if (confirm == JOptionPane.YES_OPTION) {
                    // Try execution
                    try {
                        // If open, close
                        if (poll.isOpen()) {
                            // Close via manager
                            pollManager.closePoll(poll.getPollId(), user);
                        // Otherwise reopen
                        } else {
                            // Reopen via manager
                            pollManager.reopenPoll(poll.getPollId(), user);
                        // End if
                        }
                        // Refresh admin view
                        refreshAdminView();
                        // Trigger global refresh
                        globalRefreshCallback.run();
                    // Catch exception
                    } catch (Exception ex) {
                        // Error dialog
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    // End try-catch
                    }
                // End if
                }
            // End listener
            });
            // Add toggle button
            actionPanel.add(toggleBtn);

            // Verify Receipt button
            JButton verifyReceiptBtn = new JButton("Verify Receipt");
            // Style secondary
            ModernTheme.styleSecondaryButton(verifyReceiptBtn);
            // Click listener
            verifyReceiptBtn.addActionListener(e -> handleVerifyReceipt(poll));
            // Add to panel
            actionPanel.add(verifyReceiptBtn);

            // Audit Voter Hashes button
            JButton auditBtn = new JButton("Audit Hashes (HashSet)");
            // Style secondary
            ModernTheme.styleSecondaryButton(auditBtn);
            // Click listener
            auditBtn.addActionListener(e -> showAuditDialog(poll));
            // Add audit button
            actionPanel.add(auditBtn);

            // Export Audit Report button
            JButton exportBtn = new JButton("Export Report");
            // Style secondary
            ModernTheme.styleSecondaryButton(exportBtn);
            // Click listener
            exportBtn.addActionListener(e -> handleExportPollReport(poll));
            // Add export button
            actionPanel.add(exportBtn);

            // Add action panel to east
            card.add(actionPanel, BorderLayout.EAST);

            // Add card to admin content panel
            adminContentPanel.add(card);
            // Add vertical spacing
            adminContentPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        // End for
        }

        // Revalidate
        adminContentPanel.revalidate();
        // Repaint
        adminContentPanel.repaint();
    // End of method
    }

    // Prompts admin to input a receipt code and checks cryptographic validity against the poll model
    private void handleVerifyReceipt(Poll poll) {
        // Prompt dialog
        String code = JOptionPane.showInputDialog(this, "Enter Voter Receipt Code to verify (e.g. RCPT-...):", "Verify Ballot Receipt", JOptionPane.QUESTION_MESSAGE);
        // If code supplied
        if (code != null && !code.trim().isEmpty()) {
            // Lookup receipt in poll
            VoteReceipt receipt = poll.verifyReceipt(code.trim());
            // If receipt found
            if (receipt != null) {
                // Display verification success
                JOptionPane.showMessageDialog(
                    this,
                    "RECEIPT VERIFIED LEGITIMATE:\n\n" +
                    "Receipt Code  : " + receipt.getReceiptCode() + "\n" +
                    "Poll ID       : #" + receipt.getPollId() + "\n" +
                    "Option Voted  : " + receipt.getSelectedOption() + "\n" +
                    "Voter Hash    : " + receipt.getVoterHash() + "\n" +
                    "Cast Timestamp: " + receipt.getIssuedAt() + "\n\n" +
                    "Status: This ballot is officially tallied in memory.",
                    "Receipt Verification Result",
                    JOptionPane.INFORMATION_MESSAGE
                );
            // If receipt not found
            } else {
                // Display not found error
                JOptionPane.showMessageDialog(this, "No ballot with Receipt Code '" + code.trim() + "' was found for Poll #" + poll.getPollId() + ".", "Invalid Receipt Code", JOptionPane.ERROR_MESSAGE);
            // End if
            }
        // End if
        }
    // End of method
    }

    // Handles exporting a certified report for a specific poll
    private void handleExportPollReport(Poll poll) {
        // Create file chooser
        JFileChooser chooser = new JFileChooser();
        // Set default name
        chooser.setSelectedFile(new File("poll-" + poll.getPollId().toLowerCase() + "-audit.txt"));
        // Show dialog
        int res = chooser.showSaveDialog(this);
        // If chosen
        if (res == JFileChooser.APPROVE_OPTION) {
            // File
            File target = chooser.getSelectedFile();
            // Try export
            try {
                // Compute summary
                PollResultSummary summary = resultAnalyzer.analyze(poll);
                // Export certified audit
                ReportExporter.exportCertifiedAuditReport(poll, summary, target);
                // Success message
                JOptionPane.showMessageDialog(this, "Audit report exported to:\n" + target.getAbsolutePath(), "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            // Catch error
            } catch (Exception ex) {
                // Error dialog
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            // End try-catch
            }
        // End if
        }
    // End of method
    }

    // Displays a modal showing the exact salted SHA-256 voter hashes recorded in the poll's HashSet
    private void showAuditDialog(Poll poll) {
        // Retrieve voter hashes set
        Set<String> voterHashes = poll.getVotedVoterHashes();
        // Prepare message string builder
        StringBuilder sb = new StringBuilder();
        // Append header
        sb.append("Cryptographic Audit Trail for Poll #").append(poll.getPollId()).append(":\n");
        // Append notice
        sb.append("Note: Plain-text IDs are hashed with salt using SHA-256 to ensure voter anonymity.\n");
        // Append total count
        sb.append("Total Unique Hashes in HashSet: ").append(voterHashes.size()).append("\n\n");
        // Check if empty
        if (voterHashes.isEmpty()) {
            // Append empty message
            sb.append("No votes have been recorded in this poll yet.");
        // If hashes exist
        } else {
            // Append header
            sb.append("Salted SHA-256 Voter Hashes in HashSet:\n");
            // Counter
            int count = 1;
            // Iterate over hashes
            for (String vHash : voterHashes) {
                // Append numbered truncated hash for clean dialog display
                sb.append("  ").append(count++).append(". ").append(vHash).append("\n");
            // End for
            }
        // End if
        }
        // Show message dialog
        JOptionPane.showMessageDialog(this, sb.toString(), "HashSet Cryptographic Audit - " + poll.getPollId(), JOptionPane.INFORMATION_MESSAGE);
    // End of method
    }
// End of class
}
