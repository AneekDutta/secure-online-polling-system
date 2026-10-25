// Package declaration for user interface components
package com.polling.ui;

// Import exceptions
import com.polling.exception.ValidationException;
// Import domain models
import com.polling.model.Admin;
// Import Poll
import com.polling.model.Poll;
// Import User
import com.polling.model.User;
// Import Voter
import com.polling.model.Voter;
// Import PollManager service
import com.polling.service.PollManager;
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
// Import table model
import javax.swing.table.DefaultTableModel;

// Visual panel providing administrative controls, user role switching (OOP Polymorphism), and audit logs
public class AdminPanel extends JPanel {

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

    // Creates the top section with persona switcher demonstrating OOP Polymorphism
    private JPanel createTopSection() {
        // Container panel
        JPanel topContainer = new JPanel();
        // Vertical layout
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        // Transparent
        topContainer.setOpaque(false);

        // Header title
        JLabel titleLabel = new JLabel("Administration & Security Console");
        // Heading font
        titleLabel.setFont(ModernTheme.FONT_HEADING);
        // Navy color
        titleLabel.setForeground(ModernTheme.COLOR_TEXT_PRIMARY);
        // Align left
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        // Add title
        topContainer.add(titleLabel);

        // Subtitle
        JLabel subLabel = new JLabel("Manage poll availability, audit voter hash sets, and switch user roles to test OOP Polymorphism.");
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
            JLabel metricsLabel = new JLabel("Status: " + (poll.isOpen() ? "OPEN" : "CLOSED") + "  |  Total Votes: " + poll.getTotalVotes() + "  |  Unique Voter IDs: " + poll.getVoterCount());
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

            // Audit Voter IDs button
            JButton auditBtn = new JButton("Audit Voter IDs (HashSet)");
            // Style secondary
            ModernTheme.styleSecondaryButton(auditBtn);
            // Click listener
            auditBtn.addActionListener(e -> showAuditDialog(poll));
            // Add audit button
            actionPanel.add(auditBtn);

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

    // Displays a modal showing the exact voter IDs recorded in the poll's HashSet
    private void showAuditDialog(Poll poll) {
        // Retrieve voter IDs set
        Set<String> voterIds = poll.getVotedVoterIds();
        // Prepare message string builder
        StringBuilder sb = new StringBuilder();
        // Append header
        sb.append("Audit Report for Poll ").append(poll.getPollId()).append(":\n");
        // Append total count
        sb.append("Total Unique Voters recorded in HashSet: ").append(voterIds.size()).append("\n\n");
        // Check if empty
        if (voterIds.isEmpty()) {
            // Append empty message
            sb.append("No votes have been recorded in this poll yet.");
        // If IDs exist
        } else {
            // Append header
            sb.append("Recorded Voter IDs:\n");
            // Counter
            int count = 1;
            // Iterate over IDs
            for (String vid : voterIds) {
                // Append numbered ID
                sb.append("  ").append(count++).append(". ").append(vid).append("\n");
            // End for
            }
        // End if
        }
        // Show message dialog
        JOptionPane.showMessageDialog(this, sb.toString(), "HashSet Voter Audit - " + poll.getPollId(), JOptionPane.INFORMATION_MESSAGE);
    // End of method
    }
// End of class
}
