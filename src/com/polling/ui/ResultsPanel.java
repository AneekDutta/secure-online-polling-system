// Package declaration for user interface components
package com.polling.ui;

// Import exceptions
import com.polling.exception.PollNotFoundException;
// Import Poll domain model
import com.polling.model.Poll;
// Import PollManager service
import com.polling.service.PollManager;
// Import PollResultSummary DTO
import com.polling.service.PollResultSummary;
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
// Import Font
import java.awt.Font;
// Import Graphics
import java.awt.Graphics;
// Import Graphics2D
import java.awt.Graphics2D;
// Import GridBagConstraints
import java.awt.GridBagConstraints;
// Import GridBagLayout
import java.awt.GridBagLayout;
// Import RenderingHints
import java.awt.RenderingHints;
// Import List
import java.util.List;
// Import Map
import java.util.Map;
// Import Swing components
import javax.swing.BorderFactory;
// Import Box
import javax.swing.Box;
// Import BoxLayout
import javax.swing.BoxLayout;
// Import DefaultComboBoxModel
import javax.swing.DefaultComboBoxModel;
// Import JButton
import javax.swing.JButton;
// Import JComboBox
import javax.swing.JComboBox;
// Import JComponent
import javax.swing.JComponent;
// Import JLabel
import javax.swing.JLabel;
// Import JPanel
import javax.swing.JPanel;
// Import JScrollPane
import javax.swing.JScrollPane;

// Visual panel delivering analytical vote breakdowns, percentages, horizontal progress bars, and tie detection
public class ResultsPanel extends JPanel {

    // Serial version UID
    private static final long serialVersionUID = 1L;

    // Injected PollManager reference
    private final PollManager pollManager;

    // Injected ResultAnalyzer reference
    private final ResultAnalyzer resultAnalyzer;

    // Combo box for selecting poll to view
    private final JComboBox<PollItem> pollSelector;

    // Content container for rendering analytical charts and metrics
    private final JPanel resultsContentPanel;

    // Helper item class for rendering poll titles in JComboBox
    private static class PollItem {
        // Poll ID
        final String id;
        // Truncated question
        final String label;

        // Constructor
        PollItem(String id, String question) {
            // Assign ID
            this.id = id;
            // Truncate long question strings for neat dropdown
            this.label = id + " - " + (question.length() > 50 ? question.substring(0, 47) + "..." : question);
        // End constructor
        }

        // Dropdown display string
        @Override
        public String toString() {
            // Return label
            return this.label;
        // End method
        }
    // End class
    }

    // Custom lightweight horizontal progress bar component matching modern theme
    private static class HorizontalBarComponent extends JComponent {
        // Serial UID
        private static final long serialVersionUID = 1L;
        // Percentage value (0.0 - 100.0)
        private final double percentage;
        // Flag if option is a winner
        private final boolean isWinner;

        // Constructor
        HorizontalBarComponent(double percentage, boolean isWinner) {
            // Assign percentage
            this.percentage = Math.max(0.0, Math.min(100.0, percentage));
            // Assign winner flag
            this.isWinner = isWinner;
            // Set preferred dimension
            setPreferredSize(new Dimension(300, 20));
            // Set minimum dimension
            setMinimumSize(new Dimension(100, 20));
        // End constructor
        }

        // Paint component with smooth anti-aliased rounded bar
        @Override
        protected void paintComponent(Graphics g) {
            // Delegate to superclass
            super.paintComponent(g);
            // Cast to Graphics2D
            Graphics2D g2 = (Graphics2D) g.create();
            // Enable anti-aliasing
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Width of component
            int width = getWidth();
            // Height of component
            int height = getHeight();

            // Background bar track
            g2.setColor(ModernTheme.COLOR_BG_INPUT);
            // Draw track
            g2.fillRoundRect(0, 2, width, height - 4, 8, 8);

            // Compute fill width
            int fillWidth = (int) Math.round((width * percentage) / 100.0);
            // Only fill if percentage > 0
            if (fillWidth > 0) {
                // If this option is a winner, use accent blue; otherwise subtle slate
                g2.setColor(isWinner ? ModernTheme.COLOR_ACCENT_BLUE : new Color(148, 163, 184));
                // Draw filled portion
                g2.fillRoundRect(0, 2, fillWidth, height - 4, 8, 8);
            // End if
            }

            // Dispose graphics context
            g2.dispose();
        // End method
        }
    // End class
    }

    // Constructor assembling the analytics and results interface
    public ResultsPanel(PollManager pollManager, ResultAnalyzer resultAnalyzer) {
        // Call superclass constructor
        super();
        // Assign poll manager
        this.pollManager = pollManager;
        // Assign result analyzer
        this.resultAnalyzer = resultAnalyzer;
        // Initialize poll selector combo box
        this.pollSelector = new JComboBox<>();

        // Set layout to BorderLayout
        setLayout(new BorderLayout(0, 16));
        // Set background color
        setBackground(ModernTheme.COLOR_BG_APP);
        // Set padding
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Create top selection header
        add(createSelectionHeader(), BorderLayout.NORTH);

        // Content panel for results
        this.resultsContentPanel = new JPanel();
        // Vertical layout
        this.resultsContentPanel.setLayout(new BoxLayout(this.resultsContentPanel, BoxLayout.Y_AXIS));
        // Background color
        this.resultsContentPanel.setBackground(ModernTheme.COLOR_BG_APP);

        // Wrap inside JScrollPane
        JScrollPane scrollPane = new JScrollPane(resultsContentPanel);
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

        // Populate poll selector and render initial poll
        reloadPollList(null);
    // End of constructor
    }

    // Creates the top selection header containing dropdown and refresh button
    private JPanel createSelectionHeader() {
        // Header container panel
        JPanel header = new JPanel(new BorderLayout(16, 8));
        // Transparent
        header.setOpaque(false);

        // Title section
        JPanel titlePanel = new JPanel();
        // Vertical layout
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        // Transparent
        titlePanel.setOpaque(false);

        // Heading
        JLabel titleLabel = new JLabel("Poll Results & Analytics");
        // Heading font
        titleLabel.setFont(ModernTheme.FONT_HEADING);
        // Primary color
        titleLabel.setForeground(ModernTheme.COLOR_TEXT_PRIMARY);
        // Add heading
        titlePanel.add(titleLabel);

        // Subtitle
        JLabel subLabel = new JLabel("Live vote counts, calculated percentage distributions, and winner detection.");
        // Body font
        subLabel.setFont(ModernTheme.FONT_BODY);
        // Muted color
        subLabel.setForeground(ModernTheme.COLOR_TEXT_MUTED);
        // Add spacing
        titlePanel.add(Box.createRigidArea(new Dimension(0, 4)));
        // Add subtitle
        titlePanel.add(subLabel);

        // Add title panel
        header.add(titlePanel, BorderLayout.WEST);

        // Controls panel
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        // Transparent
        controls.setOpaque(false);

        // Label for selector
        JLabel selectLabel = new JLabel("Select Poll:");
        // Bold font
        selectLabel.setFont(ModernTheme.FONT_BODY_BOLD);
        // Text color
        selectLabel.setForeground(ModernTheme.COLOR_TEXT_SECONDARY);
        // Add label
        controls.add(selectLabel);

        // Style combo box
        pollSelector.setFont(ModernTheme.FONT_BODY);
        // Background color
        pollSelector.setBackground(Color.WHITE);
        // Preferred width
        pollSelector.setPreferredSize(new Dimension(280, 36));
        // Selection change listener
        pollSelector.addActionListener(e -> renderSelectedPollResults());
        // Add selector
        controls.add(pollSelector);

        // Refresh button
        JButton refreshBtn = new JButton("Refresh");
        // Style secondary
        ModernTheme.styleSecondaryButton(refreshBtn);
        // Action listener
        refreshBtn.addActionListener(e -> {
            // Keep selected item ID
            PollItem item = (PollItem) pollSelector.getSelectedItem();
            // Reload list
            reloadPollList(item != null ? item.id : null);
        // End listener
        });
        // Add refresh button
        controls.add(refreshBtn);

        // Add controls to header
        header.add(controls, BorderLayout.EAST);

        // Return header
        return header;
    // End of method
    }

    // Reloads the dropdown with all polls and restores selection
    public void reloadPollList(String preferredPollId) {
        // Create new combo box model
        DefaultComboBoxModel<PollItem> model = new DefaultComboBoxModel<>();
        // Retrieve all polls
        List<Poll> polls = pollManager.getAllPolls();
        // Selected item tracker
        PollItem selectTarget = null;

        // Iterate over polls
        for (Poll poll : polls) {
            // Create item
            PollItem item = new PollItem(poll.getPollId(), poll.getQuestion());
            // Add to model
            model.addElement(item);
            // Check if matches preferred
            if (preferredPollId != null && preferredPollId.equalsIgnoreCase(poll.getPollId())) {
                // Set target
                selectTarget = item;
            // End if
            }
        // End for
        }

        // Set model
        pollSelector.setModel(model);

        // If target matched
        if (selectTarget != null) {
            // Select target
            pollSelector.setSelectedItem(selectTarget);
        // If items exist and no specific selection was matched
        } else if (model.getSize() > 0) {
            // Default to first item
            pollSelector.setSelectedIndex(0);
        // End if
        }

        // Render results
        renderSelectedPollResults();
    // End of method
    }

    // Programmatically selects a specific poll ID and displays its analytics
    public void selectPoll(String pollId) {
        // Reload list and focus on given poll
        reloadPollList(pollId);
    // End of method
    }

    // Computes and renders analytics for the currently selected poll
    private void renderSelectedPollResults() {
        // Clear existing UI elements
        resultsContentPanel.removeAll();

        // Retrieve selected item
        PollItem selectedItem = (PollItem) pollSelector.getSelectedItem();
        // If nothing is selected
        if (selectedItem == null) {
            // Empty state
            renderEmptyState("No poll selected. Please select a poll from the dropdown above.");
            // Revalidate
            resultsContentPanel.revalidate();
            // Repaint
            resultsContentPanel.repaint();
            // Return early
            return;
        // End if
        }

        // Fetch poll model
        Poll poll;
        // Try retrieval
        try {
            // Get poll by ID
            poll = pollManager.getPoll(selectedItem.id);
        // Catch exception
        } catch (PollNotFoundException ex) {
            // Render error state
            renderEmptyState("Error: Poll not found in storage.");
            // Revalidate
            resultsContentPanel.revalidate();
            // Repaint
            resultsContentPanel.repaint();
            // Return
            return;
        // End try-catch
        }

        // Compute analytics via ResultAnalyzer
        PollResultSummary summary = resultAnalyzer.analyze(poll);

        // 1. Overview Card (Question, Status, Total Votes, Winner Banner)
        JPanel overviewCard = new JPanel();
        // Vertical layout
        overviewCard.setLayout(new BoxLayout(overviewCard, BoxLayout.Y_AXIS));
        // White background
        overviewCard.setBackground(ModernTheme.COLOR_BG_CARD);
        // Card border
        overviewCard.setBorder(ModernTheme.createCardBorder());
        // Align left
        overviewCard.setAlignmentX(LEFT_ALIGNMENT);

        // Meta info row
        JPanel metaRow = new JPanel(new BorderLayout());
        // Transparent
        metaRow.setOpaque(false);

        // Poll ID and vote count label
        JLabel idLabel = new JLabel("Poll #" + summary.getPollId() + "  •  Total Votes: " + summary.getTotalVotes() + "  •  Unique Voters: " + poll.getVoterCount());
        // Font
        idLabel.setFont(ModernTheme.FONT_BODY_BOLD);
        // Color
        idLabel.setForeground(ModernTheme.COLOR_TEXT_MUTED);
        // Add to meta row
        metaRow.add(idLabel, BorderLayout.WEST);

        // Status badge
        JLabel statusBadge = new JLabel(summary.isOpen() ? " ● OPEN FOR VOTING " : " ● VOTING CLOSED ");
        // Font
        statusBadge.setFont(ModernTheme.FONT_SMALL_BOLD);
        // Foreground
        statusBadge.setForeground(summary.isOpen() ? ModernTheme.COLOR_STATUS_OPEN : ModernTheme.COLOR_STATUS_CLOSED);
        // Background
        statusBadge.setBackground(summary.isOpen() ? ModernTheme.COLOR_STATUS_OPEN_BG : ModernTheme.COLOR_STATUS_CLOSED_BG);
        // Opaque
        statusBadge.setOpaque(true);
        // Padding
        statusBadge.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        // Add to meta row
        metaRow.add(statusBadge, BorderLayout.EAST);

        // Add meta row
        overviewCard.add(metaRow);
        // Add spacing
        overviewCard.add(Box.createRigidArea(new Dimension(0, 12)));

        // Question display
        JLabel questionLabel = new JLabel("<html><body style='width: 750px;'>" + summary.getQuestion() + "</body></html>");
        // Title font
        questionLabel.setFont(ModernTheme.FONT_HEADING);
        // Primary text
        questionLabel.setForeground(ModernTheme.COLOR_TEXT_PRIMARY);
        // Add question
        overviewCard.add(questionLabel);
        // Add spacing
        overviewCard.add(Box.createRigidArea(new Dimension(0, 16)));

        // Winner / Leading Status Banner
        JPanel bannerPanel = new JPanel(new BorderLayout());
        // Banner padding
        bannerPanel.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        // Winner label
        JLabel bannerLabel = new JLabel();
        // Bold font
        bannerLabel.setFont(ModernTheme.FONT_BODY_BOLD);

        // Configure banner style based on results
        if (!summary.hasVotes()) {
            // Neutral grey banner for 0 votes
            bannerPanel.setBackground(ModernTheme.COLOR_BG_INPUT);
            // Label text
            bannerLabel.setText("No votes recorded yet. This poll is awaiting voter participation.");
            // Text color
            bannerLabel.setForeground(ModernTheme.COLOR_TEXT_MUTED);
        // If tie exists
        } else if (summary.isTie()) {
            // Amber banner for tie
            bannerPanel.setBackground(new Color(254, 243, 199));
            // Label text
            bannerLabel.setText("Result: TIE between options: " + String.join(" and ", summary.getWinningOptions()));
            // Amber text
            bannerLabel.setForeground(ModernTheme.COLOR_STATUS_TIE);
        // If single leader exists
        } else {
            // Light blue banner for winner
            bannerPanel.setBackground(ModernTheme.COLOR_ACCENT_LIGHT);
            // Label text
            bannerLabel.setText("Leading Option: " + summary.getWinningOptions().get(0) + " (" + summary.getOptionPercentages().get(summary.getWinningOptions().get(0)) + "%)");
            // Accent blue text
            bannerLabel.setForeground(ModernTheme.COLOR_ACCENT_BLUE);
        // End if
        }

        // Add label to banner
        bannerPanel.add(bannerLabel, BorderLayout.WEST);
        // Add banner to overview card
        overviewCard.add(bannerPanel);

        // Add overview card to results
        resultsContentPanel.add(overviewCard);
        // Add spacing
        resultsContentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 2. Detailed Breakdown Card with Horizontal Bars
        JPanel breakdownCard = new JPanel();
        // Vertical layout
        breakdownCard.setLayout(new BoxLayout(breakdownCard, BoxLayout.Y_AXIS));
        // Background
        breakdownCard.setBackground(ModernTheme.COLOR_BG_CARD);
        // Border
        breakdownCard.setBorder(ModernTheme.createCardBorder());
        // Align left
        breakdownCard.setAlignmentX(LEFT_ALIGNMENT);

        // Breakdown section title
        JLabel breakdownTitle = new JLabel("Option Vote Distribution");
        // Font
        breakdownTitle.setFont(ModernTheme.FONT_SUBHEADING);
        // Color
        breakdownTitle.setForeground(ModernTheme.COLOR_TEXT_PRIMARY);
        // Align left
        breakdownTitle.setAlignmentX(LEFT_ALIGNMENT);
        // Add title
        breakdownCard.add(breakdownTitle);
        // Spacing
        breakdownCard.add(Box.createRigidArea(new Dimension(0, 16)));

        // Map of option counts
        Map<String, Integer> counts = summary.getOptionCounts();
        // Map of option percentages
        Map<String, Double> percentages = summary.getOptionPercentages();

        // Iterate through all options to render horizontal bar and statistics
        for (String optionText : poll.getOptions()) {
            // Count for option
            int count = counts.getOrDefault(optionText, 0);
            // Percentage for option
            double pct = percentages.getOrDefault(optionText, 0.0);
            // Is this option currently among winners
            boolean isWinner = summary.hasVotes() && summary.getWinningOptions().contains(optionText);

            // Container row for this option
            JPanel optionRow = new JPanel();
            // Vertical layout for each option card row
            optionRow.setLayout(new BoxLayout(optionRow, BoxLayout.Y_AXIS));
            // Transparent
            optionRow.setOpaque(false);
            // Align left
            optionRow.setAlignmentX(LEFT_ALIGNMENT);
            // Margin
            optionRow.setBorder(BorderFactory.createEmptyBorder(6, 0, 12, 0));

            // Top text line: Option name on left, votes and % on right
            JPanel lineOne = new JPanel(new BorderLayout());
            // Transparent
            lineOne.setOpaque(false);

            // Option title label
            JLabel nameLabel = new JLabel((isWinner ? "★ " : "  ") + optionText);
            // Font
            nameLabel.setFont(isWinner ? ModernTheme.FONT_BODY_BOLD : ModernTheme.FONT_BODY);
            // Color
            nameLabel.setForeground(isWinner ? ModernTheme.COLOR_TEXT_PRIMARY : ModernTheme.COLOR_TEXT_SECONDARY);
            // Add name
            lineOne.add(nameLabel, BorderLayout.WEST);

            // Stats label (count + percentage)
            JLabel statsLabel = new JLabel(count + " votes  (" + String.format("%.1f", pct) + "%)");
            // Font
            statsLabel.setFont(ModernTheme.FONT_BODY_BOLD);
            // Color
            statsLabel.setForeground(isWinner ? ModernTheme.COLOR_ACCENT_BLUE : ModernTheme.COLOR_TEXT_MUTED);
            // Add stats
            lineOne.add(statsLabel, BorderLayout.EAST);

            // Add line one to option row
            optionRow.add(lineOne);
            // Spacing
            optionRow.add(Box.createRigidArea(new Dimension(0, 6)));

            // Horizontal graphical bar
            HorizontalBarComponent bar = new HorizontalBarComponent(pct, isWinner);
            // Maximum width
            bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
            // Add bar
            optionRow.add(bar);

            // Add option row to breakdown card
            breakdownCard.add(optionRow);
        // End for
        }

        // Add breakdown card
        resultsContentPanel.add(breakdownCard);

        // Revalidate
        resultsContentPanel.revalidate();
        // Repaint
        resultsContentPanel.repaint();
    // End of method
    }

    // Helper method rendering empty state message
    private void renderEmptyState(String message) {
        // Empty panel
        JPanel emptyPanel = new JPanel();
        // Layout
        emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
        // Transparent
        emptyPanel.setOpaque(false);
        // Padding
        emptyPanel.setBorder(BorderFactory.createEmptyBorder(60, 20, 60, 20));

        // Label
        JLabel label = new JLabel(message);
        // Font
        label.setFont(ModernTheme.FONT_SUBHEADING);
        // Color
        label.setForeground(ModernTheme.COLOR_TEXT_MUTED);
        // Center align
        label.setAlignmentX(CENTER_ALIGNMENT);
        // Add label
        emptyPanel.add(label);

        // Add to content
        resultsContentPanel.add(emptyPanel);
    // End of method
    }
// End of class
}
