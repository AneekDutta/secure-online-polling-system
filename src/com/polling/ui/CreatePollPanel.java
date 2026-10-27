//What it is: The "New Poll" Creation Form 
//ROLE:  Allows organizers to author and publish new polls.
//HOW IT IS USED:-
//1. Has a text input for the poll question.
//2. Provides dynamic option rows where users can click "+ Add Option" or "Remove" to add as many candidates/choices as needed.
//3. Validates inputs (e.g., questions cannot be blank, must have at least 2 distinct options).
//4. When published, calls PollManager.createPoll() and switches the view back to the poll list.


// Package declaration for user interface components
package com.polling.ui;

// Import validation exception
import com.polling.exception.ValidationException;
// Import Poll domain model
import com.polling.model.Poll;
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
// Import ArrayList
import java.util.ArrayList;
// Import List
import java.util.List;
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
// Import JPanel
import javax.swing.JPanel;
// Import JScrollPane
import javax.swing.JScrollPane;
// Import JTextField
import javax.swing.JTextField;

// Visual panel allowing users to compose and publish new polls with dynamic options
public class CreatePollPanel extends JPanel {

    // Serial version UID
    private static final long serialVersionUID = 1L;

    // Injected PollManager reference
    private final PollManager pollManager;

    // Callback invoked when poll is created to switch to poll list
    private final Runnable onPollCreatedCallback;

    // Text field for poll question
    private final JTextField questionField;

    // Panel holding dynamic option rows
    private final JPanel optionsListPanel;

    // Internal list keeping track of option text fields
    private final List<JTextField> optionFields;

    // Create / Publish Poll button
    private JButton createButton;

    // Status / feedback message label
    private final JLabel feedbackLabel;

    // Constructor assembling the create poll form
    public CreatePollPanel(PollManager pollManager, Runnable onPollCreatedCallback) {
        // Call superclass constructor
        super();
        // Assign poll manager
        this.pollManager = pollManager;
        // Assign on created callback
        this.onPollCreatedCallback = onPollCreatedCallback;
        // Initialize question field
        this.questionField = new JTextField(30);
        // Initialize dynamic option fields list
        this.optionFields = new ArrayList<>();
        // Initialize feedback label
        this.feedbackLabel = new JLabel(" ");

        // Set layout to BorderLayout
        setLayout(new BorderLayout(0, 16));
        // Set background color
        setBackground(ModernTheme.COLOR_BG_APP);
        // Set panel border padding
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Create main form card panel
        JPanel formCard = new JPanel();
        // Vertical layout
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        // White card background
        formCard.setBackground(ModernTheme.COLOR_BG_CARD);
        // Apply card border
        formCard.setBorder(ModernTheme.createCardBorder());

        // Header section of the form
        JPanel formHeader = new JPanel();
        // Vertical layout
        formHeader.setLayout(new BoxLayout(formHeader, BoxLayout.Y_AXIS));
        // Transparent
        formHeader.setOpaque(false);
        // Align left
        formHeader.setAlignmentX(LEFT_ALIGNMENT);

        // Title
        JLabel titleLabel = new JLabel("Create a New Poll");
        // Heading font
        titleLabel.setFont(ModernTheme.FONT_HEADING);
        // Navy color
        titleLabel.setForeground(ModernTheme.COLOR_TEXT_PRIMARY);
        // Add title
        formHeader.add(titleLabel);

        // Subtitle
        JLabel subLabel = new JLabel("Enter a clear question and at least two distinct voting options.");
        // Body font
        subLabel.setFont(ModernTheme.FONT_BODY);
        // Muted color
        subLabel.setForeground(ModernTheme.COLOR_TEXT_MUTED);
        // Add spacing
        formHeader.add(Box.createRigidArea(new Dimension(0, 4)));
        // Add subtitle
        formHeader.add(subLabel);

        // Add form header
        formCard.add(formHeader);
        // Add separator space
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        // Question input label
        JLabel questionLabel = new JLabel("Poll Question:");
        // Bold font
        questionLabel.setFont(ModernTheme.FONT_BODY_BOLD);
        // Text color
        questionLabel.setForeground(ModernTheme.COLOR_TEXT_SECONDARY);
        // Align left
        questionLabel.setAlignmentX(LEFT_ALIGNMENT);
        // Add label
        formCard.add(questionLabel);
        // Add spacing
        formCard.add(Box.createRigidArea(new Dimension(0, 6)));

        // Style question field
        ModernTheme.styleTextField(questionField);
        // Align left
        questionField.setAlignmentX(LEFT_ALIGNMENT);
        // Maximum size for horizontal expansion
        questionField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        // Add question field
        formCard.add(questionField);
        // Add spacing
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        // Options section label
        JLabel optionsHeaderLabel = new JLabel("Poll Options (Minimum 2 required):");
        // Bold font
        optionsHeaderLabel.setFont(ModernTheme.FONT_BODY_BOLD);
        // Color
        optionsHeaderLabel.setForeground(ModernTheme.COLOR_TEXT_SECONDARY);
        // Align left
        optionsHeaderLabel.setAlignmentX(LEFT_ALIGNMENT);
        // Add label
        formCard.add(optionsHeaderLabel);
        // Add spacing
        formCard.add(Box.createRigidArea(new Dimension(0, 8)));

        // Container panel for dynamic options
        this.optionsListPanel = new JPanel();
        // Vertical layout
        this.optionsListPanel.setLayout(new BoxLayout(this.optionsListPanel, BoxLayout.Y_AXIS));
        // Transparent
        this.optionsListPanel.setOpaque(false);
        // Align left
        this.optionsListPanel.setAlignmentX(LEFT_ALIGNMENT);
        // Add options list panel
        formCard.add(optionsListPanel);

        // Initial default 2 option rows
        addOptionRow();
        // Add option 2
        addOptionRow();

        // Add option action buttons panel
        JPanel optionActionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        // Transparent
        optionActionsPanel.setOpaque(false);
        // Align left
        optionActionsPanel.setAlignmentX(LEFT_ALIGNMENT);

        // Add option button
        JButton addOptionBtn = new JButton("+ Add Another Option");
        // Style secondary
        ModernTheme.styleSecondaryButton(addOptionBtn);
        // Add click listener
        addOptionBtn.addActionListener(e -> addOptionRow());
        // Add to actions panel
        optionActionsPanel.add(addOptionBtn);

        // Spacing
        formCard.add(Box.createRigidArea(new Dimension(0, 10)));
        // Add actions panel
        formCard.add(optionActionsPanel);
        // Spacing
        formCard.add(Box.createRigidArea(new Dimension(0, 24)));

        // Bottom submit row
        JPanel submitRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        // Transparent
        submitRow.setOpaque(false);
        // Align left
        submitRow.setAlignmentX(LEFT_ALIGNMENT);

        // Create Poll button
        this.createButton = new JButton("Publish Poll");
        // Style primary
        ModernTheme.stylePrimaryButton(this.createButton);
        // Add action listener
        this.createButton.addActionListener(e -> handleCreatePoll());
        // Add to submit row
        submitRow.add(this.createButton);

        // Reset form button
        JButton resetBtn = new JButton("Reset Form");
        // Style secondary
        ModernTheme.styleSecondaryButton(resetBtn);
        // Add action listener
        resetBtn.addActionListener(e -> resetForm());
        // Add to submit row
        submitRow.add(resetBtn);

        // Add submit row
        formCard.add(submitRow);

        // Feedback label
        feedbackLabel.setFont(ModernTheme.FONT_BODY_BOLD);
        // Align left
        feedbackLabel.setAlignmentX(LEFT_ALIGNMENT);
        // Spacing
        formCard.add(Box.createRigidArea(new Dimension(0, 14)));
        // Add feedback label
        formCard.add(feedbackLabel);

        // Wrap inside JScrollPane for responsiveness on small screens
        JScrollPane scrollPane = new JScrollPane(formCard);
        // Remove scroll border
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        // Background color
        scrollPane.setBackground(ModernTheme.COLOR_BG_APP);
        // Viewport background
        scrollPane.getViewport().setBackground(ModernTheme.COLOR_BG_APP);
        // Scroll speed
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Add scroll pane to center
        add(scrollPane, BorderLayout.CENTER);
    // End of constructor
    }

    // Dynamically appends a new option row with text field and remove button
    public void addOptionRow() {
        // Row index
        int index = optionFields.size() + 1;
        // Row container panel
        JPanel rowPanel = new JPanel(new BorderLayout(8, 0));
        // Transparent
        rowPanel.setOpaque(false);
        // Alignment
        rowPanel.setAlignmentX(LEFT_ALIGNMENT);
        // Maximum size
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        // Empty margin
        rowPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        // Option prefix label
        JLabel prefixLabel = new JLabel("Option " + index + ":");
        // Small bold font
        prefixLabel.setFont(ModernTheme.FONT_SMALL_BOLD);
        // Text color
        prefixLabel.setForeground(ModernTheme.COLOR_TEXT_SECONDARY);
        // Set fixed width
        prefixLabel.setPreferredSize(new Dimension(65, 34));
        // Add to row
        rowPanel.add(prefixLabel, BorderLayout.WEST);

        // New text field
        JTextField field = new JTextField();
        // Style text field
        ModernTheme.styleTextField(field);
        // Add to option fields collection
        optionFields.add(field);
        // Add to row center
        rowPanel.add(field, BorderLayout.CENTER);

        // If more than 2 options exist, provide remove button
        if (optionFields.size() > 2) {
            // Remove button
            JButton removeBtn = new JButton("Remove");
            // Style secondary
            ModernTheme.styleSecondaryButton(removeBtn);
            // Click listener
            removeBtn.addActionListener(e -> {
                // Remove field from list
                optionFields.remove(field);
                // Remove row from panel
                optionsListPanel.remove(rowPanel);
                // Renumber rows
                renumberOptionRows();
                // Revalidate
                optionsListPanel.revalidate();
                // Repaint
                optionsListPanel.repaint();
            // End listener
            });
            // Add remove button to east
            rowPanel.add(removeBtn, BorderLayout.EAST);
        // End if
        }

        // Add row to options list panel
        optionsListPanel.add(rowPanel);
        // Revalidate
        optionsListPanel.revalidate();
        // Repaint
        optionsListPanel.repaint();
    // End of method
    }

    // Renumbers the option row labels after a row deletion
    private void renumberOptionRows() {
        // Counter
        int idx = 1;
        // Loop through components of optionsListPanel
        for (java.awt.Component comp : optionsListPanel.getComponents()) {
            // Check if component is a JPanel row
            if (comp instanceof JPanel) {
                // Cast to JPanel
                JPanel row = (JPanel) comp;
                // Loop through row children
                for (java.awt.Component child : row.getComponents()) {
                    // Check for JLabel prefix
                    if (child instanceof JLabel) {
                        // Update prefix text
                        ((JLabel) child).setText("Option " + idx + ":");
                        // Break child loop
                        break;
                    // End if
                    }
                // End child loop
                }
                // Increment index
                idx++;
            // End if
            }
        // End component loop
        }
    // End of method
    }

    // Validates inputs and creates a new poll in PollManager
    private void handleCreatePoll() {
        // Extract question text
        String question = questionField.getText().trim();
        // Gather option strings
        List<String> rawOptions = new ArrayList<>();
        // Iterate over fields
        for (JTextField field : optionFields) {
            // Extract text
            String opt = field.getText().trim();
            // Add to list
            rawOptions.add(opt);
        // End for
        }

        // Try creating poll through manager
        try {
            // Delegate creation and validation to PollManager
            Poll created = pollManager.createPoll(question, rawOptions);
            // Display success message
            feedbackLabel.setText("Poll #" + created.getPollId() + " created successfully!");
            // Set green color
            feedbackLabel.setForeground(ModernTheme.COLOR_STATUS_OPEN);
            // Reset fields
            resetForm();
            // Invoke on created callback to update active views
            if (onPollCreatedCallback != null) {
                // Run callback
                onPollCreatedCallback.run();
            // End if
            }
        // Catch validation exception
        } catch (ValidationException ve) {
            // Display error message
            feedbackLabel.setText("Error: " + ve.getMessage());
            // Set red color
            feedbackLabel.setForeground(ModernTheme.COLOR_STATUS_CLOSED);
        // Catch general exception
        } catch (Exception ex) {
            // Display unexpected error
            feedbackLabel.setText("Unexpected Error: " + ex.getMessage());
            // Set red color
            feedbackLabel.setForeground(ModernTheme.COLOR_STATUS_CLOSED);
        // End try-catch
        }
    // End of method
    }

    // Resets the question field and restores default two option fields
    private void resetForm() {
        // Clear question field
        questionField.setText("");
        // Clear options collection
        optionFields.clear();
        // Clear panel components
        optionsListPanel.removeAll();
        // Add 2 fresh option rows
        addOptionRow();
        // Option 2
        addOptionRow();
        // Revalidate
        optionsListPanel.revalidate();
        // Repaint
        optionsListPanel.repaint();
    // End of method
    }

    // Returns question text field for human simulation
    public JTextField getQuestionField() {
        // Return question field
        return this.questionField;
    // End method
    }

    // Returns list of option fields for human simulation
    public List<JTextField> getOptionFields() {
        // Return option fields
        return this.optionFields;
    // End method
    }

    // Returns publish button for human simulation
    public JButton getCreateButton() {
        // Return button
        return this.createButton;
    // End method
    }
// End of class
}
