// Package declaration for user interface components
package com.polling.ui;

// Import exceptions
import com.polling.exception.DuplicateVoteException;
// Import poll closed exception
import com.polling.exception.PollClosedException;
// Import poll not found exception
import com.polling.exception.PollNotFoundException;
// Import validation exception
import com.polling.exception.ValidationException;
// Import domain models
import com.polling.model.Poll;
// Import User model
import com.polling.model.User;
// Import services
import com.polling.service.PollManager;
// Import voting service
import com.polling.service.VotingService;
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
// Import ActionEvent
import java.awt.event.ActionEvent;
// Import ActionListener
import java.awt.event.ActionListener;
// Import SimpleDateFormat for clean date formatting
import java.text.SimpleDateFormat;
// Import Swing components
import javax.swing.BorderFactory;
// Import ButtonGroup for single choice radio buttons
import javax.swing.ButtonGroup;
// Import JButton
import javax.swing.JButton;
// Import JLabel
import javax.swing.JLabel;
// Import JOptionPane for confirmation dialogs
import javax.swing.JOptionPane;
// Import JPanel
import javax.swing.JPanel;
// Import JRadioButton
import javax.swing.JRadioButton;
// Import JTextField
import javax.swing.JTextField;
// Import SwingUtilities
import javax.swing.SwingUtilities;

// Visual card panel displaying an individual poll, its ballot options, and voting actions
public class PollCardPanel extends JPanel {

    // Serial version UID
    private static final long serialVersionUID = 1L;

    // Encapsulated poll instance
    private final Poll poll;

    // Reference to voting service
    private final VotingService votingService;

    // Reference to poll manager
    private final PollManager pollManager;

    // Reference to parent refresh callback runnable
    private final Runnable refreshCallback;

    // Reference to results view navigation callback consumer
    private final java.util.function.Consumer<String> viewResultsCallback;

    // Supplier for current active user session
    private final java.util.function.Supplier<User> currentUserSupplier;

    // ButtonGroup for option radio buttons
    private final ButtonGroup optionButtonGroup;

    // Text field for voter ID entry
    private final JTextField voterIdField;

    // Status / feedback label for inline messages
    private final JLabel feedbackLabel;

    // Date formatter for creation timestamp
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy HH:mm");

    // Constructor assembling the poll card UI
    public PollCardPanel(Poll poll,
                         VotingService votingService,
                         PollManager pollManager,
                         java.util.function.Supplier<User> currentUserSupplier,
                         Runnable refreshCallback,
                         java.util.function.Consumer<String> viewResultsCallback) {
        // Call superclass constructor
        super();
        // Assign poll instance
        this.poll = poll;
        // Assign voting service
        this.votingService = votingService;
        // Assign poll manager
        this.pollManager = pollManager;
        // Assign active user supplier
        this.currentUserSupplier = currentUserSupplier;
        // Assign refresh callback
        this.refreshCallback = refreshCallback;
        // Assign view results callback
        this.viewResultsCallback = viewResultsCallback;
        // Initialize radio button group
        this.optionButtonGroup = new ButtonGroup();
        // Initialize voter ID input field
        this.voterIdField = new JTextField(12);
        // Initialize feedback message label
        this.feedbackLabel = new JLabel(" ");

        // Set card background color
        setBackground(ModernTheme.COLOR_BG_CARD);
        // Apply card border
        setBorder(ModernTheme.createCardBorder());
        // Set main layout to BorderLayout
        setLayout(new BorderLayout(0, 12));

        // Assemble and add the top header section
        add(createHeaderPanel(), BorderLayout.NORTH);
        // Assemble and add the middle options selection section
        add(createOptionsPanel(), BorderLayout.CENTER);
        // Assemble and add the bottom voting and control action section
        add(createFooterPanel(), BorderLayout.SOUTH);
    // End of constructor
    }

    // Constructs the header panel containing poll ID, status badge, and question
    private JPanel createHeaderPanel() {
        // Container panel with vertical layout
        JPanel headerPanel = new JPanel(new BorderLayout(0, 8));
        // Transparent background
        headerPanel.setOpaque(false);

        // Top meta row with ID and Status badge
        JPanel metaRow = new JPanel(new BorderLayout());
        // Transparent
        metaRow.setOpaque(false);

        // Left side: Poll identifier and creation date
        String dateStr = DATE_FORMAT.format(poll.getCreatedAt());
        // Create label
        JLabel idLabel = new JLabel(poll.getPollId() + "  •  Created: " + dateStr);
        // Style label
        idLabel.setFont(ModernTheme.FONT_SMALL_BOLD);
        // Set color
        idLabel.setForeground(ModernTheme.COLOR_TEXT_MUTED);
        // Add to meta row
        metaRow.add(idLabel, BorderLayout.WEST);

        // Right side: Status badge pill
        JLabel statusBadge = new JLabel(poll.isOpen() ? " ● OPEN " : " ● CLOSED ");
        // Small bold font
        statusBadge.setFont(ModernTheme.FONT_SMALL_BOLD);
        // Foreground color depending on status
        statusBadge.setForeground(poll.isOpen() ? ModernTheme.COLOR_STATUS_OPEN : ModernTheme.COLOR_STATUS_CLOSED);
        // Background color tint depending on status
        statusBadge.setBackground(poll.isOpen() ? ModernTheme.COLOR_STATUS_OPEN_BG : ModernTheme.COLOR_STATUS_CLOSED_BG);
        // Make opaque to show background pill
        statusBadge.setOpaque(true);
        // Border around status pill
        statusBadge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        // Add to meta row
        metaRow.add(statusBadge, BorderLayout.EAST);

        // Add meta row to header
        headerPanel.add(metaRow, BorderLayout.NORTH);

        // Question label
        JLabel questionLabel = new JLabel("<html><body style='width: 600px;'>" + poll.getQuestion() + "</body></html>");
        // Style font
        questionLabel.setFont(ModernTheme.FONT_CARD_TITLE);
        // Primary text color
        questionLabel.setForeground(ModernTheme.COLOR_TEXT_PRIMARY);
        // Add question to header
        headerPanel.add(questionLabel, BorderLayout.CENTER);

        // Return header
        return headerPanel;
    // End of method
    }

    // Constructs the options panel listing candidate radio buttons
    private JPanel createOptionsPanel() {
        // Container panel with GridBagLayout for clean vertical alignment
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        // Transparent background
        optionsPanel.setOpaque(false);
        // GridBagConstraints helper
        GridBagConstraints gbc = new GridBagConstraints();
        // Left align
        gbc.anchor = GridBagConstraints.WEST;
        // Horizontal fill
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // Insets spacing between option rows
        gbc.insets = new Insets(4, 0, 4, 0);
        // Grid column 0
        gbc.gridx = 0;
        // Row index counter
        int row = 0;

        // Iterate through all candidate options
        for (String optionText : poll.getOptions()) {
            // Set grid row
            gbc.gridy = row++;
            // Create radio button
            JRadioButton radioButton = new JRadioButton(optionText);
            // Assign action command to option text
            radioButton.setActionCommand(optionText);
            // Set font
            radioButton.setFont(ModernTheme.FONT_BODY);
            // Set foreground text color
            radioButton.setForeground(ModernTheme.COLOR_TEXT_SECONDARY);
            // Transparent background
            radioButton.setOpaque(false);
            // Disable if poll is closed
            radioButton.setEnabled(poll.isOpen());
            // Add radio button to mutual exclusion group
            optionButtonGroup.add(radioButton);
            // Add to panel
            optionsPanel.add(radioButton, gbc);
        // End for
        }

        // Return options panel
        return optionsPanel;
    // End of method
    }

    // Constructs the footer panel with voter ID entry, vote submission, and admin controls
    private JPanel createFooterPanel() {
        // Main footer container
        JPanel footerPanel = new JPanel(new BorderLayout(0, 8));
        // Transparent
        footerPanel.setOpaque(false);

        // Action controls row
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        // Transparent
        actionRow.setOpaque(false);

        // Label for voter ID input
        JLabel voterLabel = new JLabel("Voter ID:");
        // Bold small font
        voterLabel.setFont(ModernTheme.FONT_BODY_BOLD);
        // Secondary color
        voterLabel.setForeground(ModernTheme.COLOR_TEXT_SECONDARY);
        // Add to action row
        actionRow.add(voterLabel);

        // Style text field
        ModernTheme.styleTextField(voterIdField);
        // Disable text field if poll is closed
        voterIdField.setEnabled(poll.isOpen());
        // Add to action row
        actionRow.add(voterIdField);

        // Create vote button
        JButton voteButton = new JButton("Submit Vote");
        // Style as primary button
        ModernTheme.stylePrimaryButton(voteButton);
        // Disable button if poll is closed
        voteButton.setEnabled(poll.isOpen());
        // Attach click listener for voting
        voteButton.addActionListener(new ActionListener() {
            // Invoked when vote button is clicked
            @Override
            public void actionPerformed(ActionEvent e) {
                // Execute vote submission
                handleVoteSubmission();
            // End of method
            }
        // End of listener
        });
        // Add to action row
        actionRow.add(voteButton);

        // Create View Results button
        JButton viewResultsButton = new JButton("View Results (" + poll.getTotalVotes() + ")");
        // Style as secondary button
        ModernTheme.styleSecondaryButton(viewResultsButton);
        // Attach click listener
        viewResultsButton.addActionListener(new ActionListener() {
            // Invoked on click
            @Override
            public void actionPerformed(ActionEvent e) {
                // Navigate to results view for this poll
                if (viewResultsCallback != null) {
                    // Trigger callback with poll ID
                    viewResultsCallback.accept(poll.getPollId());
                // End if
                }
            // End of method
            }
        // End of listener
        });
        // Add to action row
        actionRow.add(viewResultsButton);

        // Check if current user is an Admin to show administrative controls
        User currentUser = currentUserSupplier.get();
        // If user has admin capabilities
        if (currentUser != null && currentUser.canManagePolls()) {
            // Button label based on status
            String toggleText = poll.isOpen() ? "Close Poll" : "Reopen Poll";
            // Create admin toggle button
            JButton adminToggleButton = new JButton(toggleText);
            // Style as secondary button
            ModernTheme.styleSecondaryButton(adminToggleButton);
            // Attach click listener
            adminToggleButton.addActionListener(new ActionListener() {
                // Invoked on click
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Handle admin status toggle
                    handleAdminToggleStatus();
                // End of method
                }
            // End of listener
            });
            // Add to action row
            actionRow.add(adminToggleButton);
        // End if
        }

        // Add action row to footer
        footerPanel.add(actionRow, BorderLayout.NORTH);

        // Feedback label styling
        feedbackLabel.setFont(ModernTheme.FONT_SMALL_BOLD);
        // Add feedback label to bottom
        footerPanel.add(feedbackLabel, BorderLayout.SOUTH);

        // Return footer panel
        return footerPanel;
    // End of method
    }

    // Handles the vote submission logic and catches all custom domain exceptions
    private void handleVoteSubmission() {
        // Extract voter ID text
        String voterId = voterIdField.getText().trim();
        // Extract selected option command
        String selectedOption = null;
        // Check if a radio button is selected
        if (optionButtonGroup.getSelection() != null) {
            // Retrieve action command of selected option
            selectedOption = optionButtonGroup.getSelection().getActionCommand();
        // End if
        }

        // Try casting vote through VotingService
        try {
            // Submit vote to business service layer
            votingService.castVote(poll.getPollId(), voterId, selectedOption);
            // Display success feedback message
            showFeedback("Vote successfully recorded! Thank you for participating.", ModernTheme.COLOR_STATUS_OPEN);
            // Clear input field
            voterIdField.setText("");
            // Clear radio button selection
            optionButtonGroup.clearSelection();
            // Trigger parent refresh to update tallies
            if (refreshCallback != null) {
                // Refresh parent view
                refreshCallback.run();
            // End if
            }
        // Catch duplicate vote exception
        } catch (DuplicateVoteException dve) {
            // Show duplicate vote error
            showFeedback("Error: " + dve.getMessage(), ModernTheme.COLOR_STATUS_CLOSED);
        // Catch poll closed exception
        } catch (PollClosedException pce) {
            // Show closed error
            showFeedback("Error: " + pce.getMessage(), ModernTheme.COLOR_STATUS_CLOSED);
        // Catch invalid vote exception
        } catch (com.polling.exception.InvalidVoteException ive) {
            // Show invalid option error
            showFeedback("Error: " + ive.getMessage(), ModernTheme.COLOR_STATUS_CLOSED);
        // Catch validation exception
        } catch (ValidationException ve) {
            // Show validation error
            showFeedback("Validation Error: " + ve.getMessage(), ModernTheme.COLOR_STATUS_CLOSED);
        // Catch poll not found exception
        } catch (PollNotFoundException pnfe) {
            // Show poll missing error
            showFeedback("Error: " + pnfe.getMessage(), ModernTheme.COLOR_STATUS_CLOSED);
        // Catch general runtime exception
        } catch (Exception ex) {
            // Show unexpected failure message
            showFeedback("Unexpected Error: " + ex.getMessage(), ModernTheme.COLOR_STATUS_CLOSED);
        // End try-catch
        }
    // End of method
    }

    // Handles administrative toggle with confirmation dialog
    private void handleAdminToggleStatus() {
        // Action name
        String actionName = poll.isOpen() ? "close" : "reopen";
        // Prompt confirmation dialog
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to " + actionName + " Poll #" + poll.getPollId() + "?\n" +
            (poll.isOpen() ? "Voters will no longer be able to cast votes in this poll." : "Voters will be able to resume voting."),
            "Confirm Admin Action",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        // If user confirmed YES
        if (confirm == JOptionPane.YES_OPTION) {
            // Try updating poll state
            try {
                // Check current state
                if (poll.isOpen()) {
                    // Close poll via poll manager
                    pollManager.closePoll(poll.getPollId(), currentUserSupplier.get());
                // Otherwise reopen
                } else {
                    // Reopen poll via poll manager
                    pollManager.reopenPoll(poll.getPollId(), currentUserSupplier.get());
                // End if
                }
                // Refresh view
                if (refreshCallback != null) {
                    // Execute refresh
                    refreshCallback.run();
                // End if
                }
            // Catch domain exceptions
            } catch (Exception ex) {
                // Show error dialog
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Operation Failed", JOptionPane.ERROR_MESSAGE);
            // End try-catch
            }
        // End if
        }
    // End of method
    }

    // Displays an inline feedback message with specified color
    private void showFeedback(String message, Color color) {
        // Set message text
        feedbackLabel.setText(message);
        // Set foreground color
        feedbackLabel.setForeground(color);
    // End of method
    }
// End of class
}
