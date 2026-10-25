// Package declaration for user interface components
package com.polling.ui;

// Import Poll model
import com.polling.model.Poll;
// Import User model
import com.polling.model.User;
// Import PollManager service
import com.polling.service.PollManager;
// Import VotingService service
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
import java.awt.GridBagLayout;
// Import Insets
import java.awt.Insets;
// Import event listeners
import java.awt.event.ActionEvent;
// Import ActionListener
import java.awt.event.ActionListener;
// Import KeyAdapter
import java.awt.event.KeyAdapter;
// Import KeyEvent
import java.awt.event.KeyEvent;
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

// Panel that renders the list of available polls with search and filter capabilities
public class PollListPanel extends JPanel {

    // Serial version UID
    private static final long serialVersionUID = 1L;

    // Injected PollManager reference
    private final PollManager pollManager;

    // Injected VotingService reference
    private final VotingService votingService;

    // Active user session supplier
    private final java.util.function.Supplier<User> currentUserSupplier;

    // Callback for navigating to results view
    private final java.util.function.Consumer<String> viewResultsCallback;

    // Vertical container holding the list of poll cards
    private final JPanel cardsContainer;

    // Search input text field
    private final JTextField searchField;

    // Currently active filter: "ALL", "OPEN", "CLOSED"
    private String currentFilter = "ALL";

    // Constructor assembling the poll catalog panel
    public PollListPanel(PollManager pollManager,
                         VotingService votingService,
                         java.util.function.Supplier<User> currentUserSupplier,
                         java.util.function.Consumer<String> viewResultsCallback) {
        // Call superclass constructor
        super();
        // Assign poll manager
        this.pollManager = pollManager;
        // Assign voting service
        this.votingService = votingService;
        // Assign user supplier
        this.currentUserSupplier = currentUserSupplier;
        // Assign results callback
        this.viewResultsCallback = viewResultsCallback;

        // Set layout to BorderLayout
        setLayout(new BorderLayout(0, 16));
        // Set background color
        setBackground(ModernTheme.COLOR_BG_APP);
        // Add padding around entire panel
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Create cards container panel
        this.cardsContainer = new JPanel();
        // Set vertical BoxLayout for sequential card layout
        this.cardsContainer.setLayout(new BoxLayout(this.cardsContainer, BoxLayout.Y_AXIS));
        // Set background of container
        this.cardsContainer.setBackground(ModernTheme.COLOR_BG_APP);

        // Initialize search field
        this.searchField = new JTextField(16);
        // Style search field
        ModernTheme.styleTextField(this.searchField);

        // Assemble and add top filter and search header bar
        add(createTopBar(), BorderLayout.NORTH);

        // Wrap cards container inside modern JScrollPane
        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        // Remove default border
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        // Background color
        scrollPane.setBackground(ModernTheme.COLOR_BG_APP);
        // Viewport background
        scrollPane.getViewport().setBackground(ModernTheme.COLOR_BG_APP);
        // Enable smooth scrolling speed
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Add scroll pane to center
        add(scrollPane, BorderLayout.CENTER);

        // Populate initial cards
        refreshPolls();
    // End of constructor
    }

    // Creates the top navigation and filter control bar
    private JPanel createTopBar() {
        // Container panel with BorderLayout
        JPanel topBar = new JPanel(new BorderLayout(16, 8));
        // Transparent background
        topBar.setOpaque(false);

        // Left section: Title & subtitle
        JPanel titlePanel = new JPanel();
        // Set vertical layout
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        // Transparent
        titlePanel.setOpaque(false);

        // Heading label
        JLabel titleLabel = new JLabel("Available Polls");
        // Apply heading font
        titleLabel.setFont(ModernTheme.FONT_HEADING);
        // Apply navy color
        titleLabel.setForeground(ModernTheme.COLOR_TEXT_PRIMARY);
        // Add title
        titlePanel.add(titleLabel);

        // Subtitle label
        JLabel subLabel = new JLabel("Select an option and submit your voter ID to cast your ballot.");
        // Apply body font
        subLabel.setFont(ModernTheme.FONT_BODY);
        // Apply muted color
        subLabel.setForeground(ModernTheme.COLOR_TEXT_MUTED);
        // Add subtitle
        titlePanel.add(subLabel);

        // Add title panel to west
        topBar.add(titlePanel, BorderLayout.WEST);

        // Right section: Filter buttons and Search field
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        // Transparent
        controlsPanel.setOpaque(false);

        // Filter button: All
        JButton allButton = new JButton("All");
        // Style secondary
        ModernTheme.styleSecondaryButton(allButton);
        // Add action listener
        allButton.addActionListener(e -> {
            // Set filter
            currentFilter = "ALL";
            // Refresh cards
            refreshPolls();
        // End listener
        });
        // Add button
        controlsPanel.add(allButton);

        // Filter button: Open
        JButton openButton = new JButton("Open Only");
        // Style secondary
        ModernTheme.styleSecondaryButton(openButton);
        // Add action listener
        openButton.addActionListener(e -> {
            // Set filter
            currentFilter = "OPEN";
            // Refresh cards
            refreshPolls();
        // End listener
        });
        // Add button
        controlsPanel.add(openButton);

        // Filter button: Closed
        JButton closedButton = new JButton("Closed Only");
        // Style secondary
        ModernTheme.styleSecondaryButton(closedButton);
        // Add action listener
        closedButton.addActionListener(e -> {
            // Set filter
            currentFilter = "CLOSED";
            // Refresh cards
            refreshPolls();
        // End listener
        });
        // Add button
        controlsPanel.add(closedButton);

        // Search label
        JLabel searchLabel = new JLabel("Search:");
        // Font
        searchLabel.setFont(ModernTheme.FONT_BODY_BOLD);
        // Text color
        searchLabel.setForeground(ModernTheme.COLOR_TEXT_SECONDARY);
        // Add search label
        controlsPanel.add(searchLabel);

        // Attach live key listener to search input
        searchField.addKeyListener(new KeyAdapter() {
            // Key released event
            @Override
            public void keyReleased(KeyEvent e) {
                // Refresh list on typing
                refreshPolls();
            // End method
            }
        // End adapter
        });
        // Add search input field
        controlsPanel.add(searchField);

        // Refresh button
        JButton refreshBtn = new JButton("Refresh");
        // Style secondary
        ModernTheme.styleSecondaryButton(refreshBtn);
        // Add listener
        refreshBtn.addActionListener(e -> refreshPolls());
        // Add to controls
        controlsPanel.add(refreshBtn);

        // Add controls panel to east
        topBar.add(controlsPanel, BorderLayout.EAST);

        // Return top bar
        return topBar;
    // End of method
    }

    // Reloads and renders all matching poll cards
    public void refreshPolls() {
        // Clear all existing cards from container
        cardsContainer.removeAll();

        // Retrieve full list of polls from PollManager
        List<Poll> allPolls = pollManager.getAllPolls();
        // Extract search query text
        String query = searchField.getText().trim().toLowerCase();
        // Counter for matching polls rendered
        int count = 0;

        // Iterate through all polls
        for (Poll poll : allPolls) {
            // Filter by open/closed status
            if ("OPEN".equals(currentFilter) && !poll.isOpen()) {
                // Skip closed poll when filtering open
                continue;
            // End if
            }
            // Filter by closed status
            if ("CLOSED".equals(currentFilter) && poll.isOpen()) {
                // Skip open poll when filtering closed
                continue;
            // End if
            }
            // Filter by search query text
            if (!query.isEmpty()) {
                // Check if query matches ID or question
                boolean matchesId = poll.getPollId().toLowerCase().contains(query);
                // Check if query matches question
                boolean matchesQuestion = poll.getQuestion().toLowerCase().contains(query);
                // If neither matches, skip
                if (!matchesId && !matchesQuestion) {
                    // Skip poll
                    continue;
                // End if
                }
            // End if
            }

            // Create new card panel for this poll
            PollCardPanel card = new PollCardPanel(
                poll,
                votingService,
                pollManager,
                currentUserSupplier,
                this::refreshPolls,
                viewResultsCallback
            );

            // Add card to container
            cardsContainer.add(card);
            // Add vertical spacing between cards
            cardsContainer.add(Box.createRigidArea(new Dimension(0, 16)));
            // Increment count
            count++;
        // End for
        }

        // If no polls matched filters or search query, display clean empty state
        if (count == 0) {
            // Empty state panel
            JPanel emptyPanel = new JPanel();
            // BoxLayout
            emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
            // Transparent
            emptyPanel.setOpaque(false);
            // Top padding
            emptyPanel.setBorder(BorderFactory.createEmptyBorder(60, 20, 60, 20));

            // Empty state main label
            JLabel emptyLabel = new JLabel("No polls found matching the current criteria.");
            // Font
            emptyLabel.setFont(ModernTheme.FONT_SUBHEADING);
            // Color
            emptyLabel.setForeground(ModernTheme.COLOR_TEXT_MUTED);
            // Center align
            emptyLabel.setAlignmentX(CENTER_ALIGNMENT);
            // Add to empty panel
            emptyPanel.add(emptyLabel);

            // Empty state sub label
            JLabel emptySubLabel = new JLabel("Try clearing your search query or creating a new poll from the 'Create Poll' tab.");
            // Font
            emptySubLabel.setFont(ModernTheme.FONT_BODY);
            // Color
            emptySubLabel.setForeground(ModernTheme.COLOR_TEXT_MUTED);
            // Center align
            emptySubLabel.setAlignmentX(CENTER_ALIGNMENT);
            // Add spacing
            emptyPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            // Add to empty panel
            emptyPanel.add(emptySubLabel);

            // Add empty panel to container
            cardsContainer.add(emptyPanel);
        // End if
        }

        // Revalidate layout hierarchy
        cardsContainer.revalidate();
        // Repaint container
        cardsContainer.repaint();
    // End of method
    }
// End of class
}
