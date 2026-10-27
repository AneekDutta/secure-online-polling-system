// Package declaration for user interface components
package com.polling.ui;

// Import domain models
import com.polling.model.Admin;
// Import User
import com.polling.model.User;
// Import Voter
import com.polling.model.Voter;
// Import service layers
import com.polling.service.PollManager;
// Import ResultAnalyzer
import com.polling.service.ResultAnalyzer;
// Import VotingService
import com.polling.service.VotingService;
// Import AWT layout components
import java.awt.BorderLayout;
// Import CardLayout
import java.awt.CardLayout;
// Import Color
import java.awt.Color;
// Import Dimension
import java.awt.Dimension;
// Import FlowLayout
import java.awt.FlowLayout;
// Import Font
import java.awt.Font;
// Import GridBagConstraints
import java.awt.GridBagConstraints;
// Import GridBagLayout
import java.awt.GridBagLayout;
// Import Insets
import java.awt.Insets;
// Import Swing components
import javax.swing.BorderFactory;
// Import Box
import javax.swing.Box;
// Import BoxLayout
import javax.swing.BoxLayout;
// Import JButton
import javax.swing.JButton;
// Import JFrame
import javax.swing.JFrame;
// Import JLabel
import javax.swing.JLabel;
// Import JPanel
import javax.swing.JPanel;
// Import SwingUtilities
import javax.swing.SwingUtilities;

// Primary desktop application window hosting navigation, persona status, and views
public class MainFrame extends JFrame {

    // Serial version UID
    private static final long serialVersionUID = 1L;

    // Service managing polls
    private final PollManager pollManager;

    // Service handling voting operations
    private final VotingService votingService;

    // Service analyzing poll results
    private final ResultAnalyzer resultAnalyzer;

    // Active user session (Polymorphism: Admin or Voter)
    private User currentUser;

    // Main CardLayout for page navigation
    private final CardLayout cardLayout;

    // Main container panel holding card pages
    private final JPanel pagesPanel;

    // Child panels
    private final PollListPanel pollListPanel;
    // Create poll panel
    private final CreatePollPanel createPollPanel;
    // Results panel
    private final ResultsPanel resultsPanel;
    // Admin panel
    private final AdminPanel adminPanel;

    // Navigation buttons
    private final JButton navPollsBtn;
    // Nav create button
    private final JButton navCreateBtn;
    // Nav results button
    private final JButton navResultsBtn;
    // Nav admin button
    private final JButton navAdminBtn;

    // User status badge label in header
    private final JLabel userBadgeLabel;

    // Cloud database status badge label in header
    private final JLabel cloudBadgeLabel;

    // Constructor initializing services and building main window
    public MainFrame() {
        // Set window title
        super("Secure Online Polling & Result Analysis System");

        // Initialize core services
        this.pollManager = new PollManager();
        // Initialize voting service
        this.votingService = new VotingService(this.pollManager);
        // Initialize result analyzer
        this.resultAnalyzer = new ResultAnalyzer();

        // Default active user is an Admin session for easy evaluation
        this.currentUser = new Admin("ADMIN-101", "Gauri Admin", "Campus Election Commission");

        // Set default close operation
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set standard laptop resolution
        setSize(1080, 720);
        // Set minimum responsive size
        setMinimumSize(new Dimension(920, 600));
        // Center on screen
        setLocationRelativeTo(null);

        // Main content pane background
        getContentPane().setBackground(ModernTheme.COLOR_BG_APP);
        // Main layout
        setLayout(new BorderLayout());

        // Header user badge label
        this.userBadgeLabel = new JLabel();
        // Header cloud database status badge label
        this.cloudBadgeLabel = new JLabel("  ☁ Supabase: Checking...  ");
        // Style cloud badge label
        this.cloudBadgeLabel.setFont(ModernTheme.FONT_SMALL_BOLD);
        // Default color
        this.cloudBadgeLabel.setForeground(ModernTheme.COLOR_TEXT_MUTED);
        // Default background
        this.cloudBadgeLabel.setBackground(ModernTheme.COLOR_BG_INPUT);
        // Make opaque
        this.cloudBadgeLabel.setOpaque(true);
        // Border padding
        this.cloudBadgeLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ModernTheme.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));

        // Initialize CardLayout for tabbed page switching
        this.cardLayout = new CardLayout();
        // Container panel for cards
        this.pagesPanel = new JPanel(this.cardLayout);
        // Background
        this.pagesPanel.setBackground(ModernTheme.COLOR_BG_APP);

        // Instantiate navigation buttons
        this.navPollsBtn = new JButton("Active Polls");
        // Nav create button
        this.navCreateBtn = new JButton("Create Poll");
        // Nav results button
        this.navResultsBtn = new JButton("Results & Analytics");
        // Nav admin button
        this.navAdminBtn = new JButton("Admin Console");

        // Instantiate child views
        this.pollListPanel = new PollListPanel(
            pollManager,
            votingService,
            this::getCurrentUser,
            this::showPollResults
        );

        // Instantiate results view
        this.resultsPanel = new ResultsPanel(
            pollManager,
            resultAnalyzer
        );

        // Instantiate create poll view
        this.createPollPanel = new CreatePollPanel(
            pollManager,
            () -> {
                // Refresh list
                pollListPanel.refreshPolls();
                // Refresh results
                resultsPanel.reloadPollList(null);
                // Switch to polls list tab
                showCard("POLLS");
            // End callback
            }
        );

        // Instantiate admin view
        this.adminPanel = new AdminPanel(
            pollManager,
            this::getCurrentUser,
            this::setCurrentUser,
            this::refreshAllViews
        );

        // Register observers with PollManager for automatic event-driven updates (Observer Pattern)
        this.pollManager.addEventListener(this.pollListPanel);
        // Register results panel observer
        this.pollManager.addEventListener(this.resultsPanel);
        // Register admin panel observer
        this.pollManager.addEventListener(this.adminPanel);

        // Add pages to card layout panel
        pagesPanel.add(pollListPanel, "POLLS");
        // Add create page
        pagesPanel.add(createPollPanel, "CREATE");
        // Add results page
        pagesPanel.add(resultsPanel, "RESULTS");
        // Add admin page
        pagesPanel.add(adminPanel, "ADMIN");

        // Assemble and add top header bar
        add(createHeaderPanel(), BorderLayout.NORTH);
        // Add cards container to center
        add(pagesPanel, BorderLayout.CENTER);
        // Assemble and add bottom notification banner
        add(createNoticeBanner(), BorderLayout.SOUTH);

        // Update header badge with initial user
        updateUserBadge();
        // Activate initial tab styling
        updateNavButtonStyles(navPollsBtn);
        // Verify and update Supabase cloud connectivity status asynchronously
        checkCloudStatus();
    // End of constructor
    }

    // Public getter for active user session
    public User getCurrentUser() {
        // Return current user
        return this.currentUser;
    // End of method
    }

    // Public setter for active user session (supporting role switching)
    public void setCurrentUser(User user) {
        // Assign new user session
        this.currentUser = user;
        // Update header badge
        updateUserBadge();
        // Refresh views
        refreshAllViews();
    // End of method
    }

    // Switches the active page card and updates navigation button styling
    private void showCard(String cardName) {
        // Show specified card
        cardLayout.show(pagesPanel, cardName);
        // Update nav styles based on active card
        if ("POLLS".equals(cardName)) {
            // Highlight polls
            updateNavButtonStyles(navPollsBtn);
        // Check create
        } else if ("CREATE".equals(cardName)) {
            // Highlight create
            updateNavButtonStyles(navCreateBtn);
        // Check results
        } else if ("RESULTS".equals(cardName)) {
            // Highlight results
            updateNavButtonStyles(navResultsBtn);
        // Check admin
        } else if ("ADMIN".equals(cardName)) {
            // Highlight admin
            updateNavButtonStyles(navAdminBtn);
        // End if
        }
    // End of method
    }

    // Switches to the Results page and selects a specific poll ID directly
    public void showPollResults(String pollId) {
        // Select target poll in results panel
        resultsPanel.selectPoll(pollId);
        // Switch card to results
        showCard("RESULTS");
    // End of method
    }

    // Refreshes data across all sub-panels
    public void refreshAllViews() {
        // Refresh polls list
        pollListPanel.refreshPolls();
        // Refresh results
        resultsPanel.reloadPollList(null);
        // Refresh admin
        adminPanel.refreshAdminView();
    // End of method
    }

    // Assembles the top header panel containing branding, nav bar, and active user persona
    private JPanel createHeaderPanel() {
        // Container panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        // White background
        headerPanel.setBackground(ModernTheme.COLOR_BG_CARD);
        // Border dividing header from content
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ModernTheme.COLOR_BORDER));

        // Top branding row
        JPanel brandRow = new JPanel(new BorderLayout(16, 0));
        // Transparent
        brandRow.setOpaque(false);
        // Padding
        brandRow.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));

        // Left title panel
        JPanel titlePanel = new JPanel();
        // Vertical layout
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        // Transparent
        titlePanel.setOpaque(false);

        // App title
        JLabel appTitle = new JLabel("Online Polling & Result Analysis System");
        // Heading font
        appTitle.setFont(ModernTheme.FONT_HEADING);
        // Navy color
        appTitle.setForeground(ModernTheme.COLOR_TEXT_PRIMARY);
        // Add title
        titlePanel.add(appTitle);

        // Subtitle
        JLabel appSub = new JLabel("College OOP Project  •  Java Swing & Supabase PostgreSQL Cloud Platform");
        // Small font
        appSub.setFont(ModernTheme.FONT_SMALL);
        // Muted color
        appSub.setForeground(ModernTheme.COLOR_TEXT_MUTED);
        // Add spacing
        titlePanel.add(Box.createRigidArea(new Dimension(0, 2)));
        // Add subtitle
        titlePanel.add(appSub);

        // Add title panel to west
        brandRow.add(titlePanel, BorderLayout.WEST);

        // Right user badge container
        JPanel userContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        // Transparent
        userContainer.setOpaque(false);
        // Add cloud database status badge
        userContainer.add(cloudBadgeLabel);
        // Add user persona badge
        userContainer.add(userBadgeLabel);
        // Add to brand row east
        brandRow.add(userContainer, BorderLayout.EAST);

        // Add brand row to header
        headerPanel.add(brandRow, BorderLayout.NORTH);

        // Navigation tab bar row
        JPanel navRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        // Background
        navRow.setBackground(ModernTheme.COLOR_BG_APP);
        // Padding
        navRow.setBorder(BorderFactory.createEmptyBorder(0, 16, 4, 16));

        // Configure Polls button
        navPollsBtn.addActionListener(e -> showCard("POLLS"));
        // Add to nav row
        navRow.add(navPollsBtn);

        // Configure Create button
        navCreateBtn.addActionListener(e -> showCard("CREATE"));
        // Add to nav row
        navRow.add(navCreateBtn);

        // Configure Results button
        navResultsBtn.addActionListener(e -> showCard("RESULTS"));
        // Add to nav row
        navRow.add(navResultsBtn);

        // Configure Admin button
        navAdminBtn.addActionListener(e -> showCard("ADMIN"));
        // Add to nav row
        navRow.add(navAdminBtn);

        // Add nav row to header south
        headerPanel.add(navRow, BorderLayout.SOUTH);

        // Return header panel
        return headerPanel;
    // End of method
    }

    // Creates the bottom notification banner reminding user of in-memory behavior
    private JPanel createNoticeBanner() {
        // Notice panel
        JPanel noticePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 6));
        // Light subtle background
        noticePanel.setBackground(ModernTheme.COLOR_BG_INPUT);
        // Border
        noticePanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ModernTheme.COLOR_BORDER));

        // Notice message
        JLabel noticeLabel = new JLabel("Notice: In-Memory Storage & Supabase Cloud PostgreSQL Sync Active — Hybrid Local/Cloud Architecture.");
        // Font
        noticeLabel.setFont(ModernTheme.FONT_SMALL);
        // Muted color
        noticeLabel.setForeground(ModernTheme.COLOR_TEXT_MUTED);
        // Add label
        noticePanel.add(noticeLabel);

        // Return notice panel
        return noticePanel;
    // End of method
    }

    // Updates the visual appearance of navigation buttons to reflect active selection
    private void updateNavButtonStyles(JButton activeButton) {
        // Array of all nav buttons
        JButton[] buttons = {navPollsBtn, navCreateBtn, navResultsBtn, navAdminBtn};
        // Loop over buttons
        for (JButton btn : buttons) {
            // Check if this is the active button
            if (btn == activeButton) {
                // Style as active with accent blue background
                btn.setBackground(ModernTheme.COLOR_ACCENT_BLUE);
                // White text
                btn.setForeground(Color.WHITE);
                // Bold font
                btn.setFont(ModernTheme.FONT_BODY_BOLD);
                // Filled
                btn.setContentAreaFilled(true);
                // Border painted false
                btn.setBorderPainted(false);
                // Opaque
                btn.setOpaque(true);
                // Padding
                btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            // If inactive button
            } else {
                // Background transparent / card
                btn.setBackground(ModernTheme.COLOR_BG_APP);
                // Slate text
                btn.setForeground(ModernTheme.COLOR_TEXT_SECONDARY);
                // Regular font
                btn.setFont(ModernTheme.FONT_BODY_BOLD);
                // Not filled
                btn.setContentAreaFilled(false);
                // Border
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ModernTheme.COLOR_BORDER, 1),
                    BorderFactory.createEmptyBorder(7, 15, 7, 15)
                ));
                // Opaque
                btn.setOpaque(true);
            // End if
            }
        // End for
        }
    // End of method
    }

    // Updates the header badge showing the active polymorphic user role
    private void updateUserBadge() {
        // If current user is present
        if (currentUser != null) {
            // Text showing role and name
            userBadgeLabel.setText("  " + currentUser.getRole() + ": " + currentUser.getName() + "  ");
            // Bold small font
            userBadgeLabel.setFont(ModernTheme.FONT_SMALL_BOLD);
            // Text color
            userBadgeLabel.setForeground(currentUser.canManagePolls() ? ModernTheme.COLOR_ACCENT_BLUE : ModernTheme.COLOR_TEXT_SECONDARY);
            // Background tint
            userBadgeLabel.setBackground(currentUser.canManagePolls() ? ModernTheme.COLOR_ACCENT_LIGHT : ModernTheme.COLOR_BG_INPUT);
            // Opaque
            userBadgeLabel.setOpaque(true);
            // Padding
            userBadgeLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
            ));
        // End if
        }
    // End of method
    }

    // Asynchronously checks Supabase cloud connectivity and updates the header badge
    private void checkCloudStatus() {
        // Run network check in a separate background thread so the UI thread never freezes
        java.util.concurrent.CompletableFuture.supplyAsync(() -> {
            // Ping Supabase cloud database
            return pollManager.getSupabaseClient().isAvailable();
        // Handle result on Event Dispatch Thread
        }).thenAccept(connected -> {
            // Update UI on Swing Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                // If connected successfully
                if (connected) {
                    // Update text
                    cloudBadgeLabel.setText("  ☁ Supabase: Connected  ");
                    // Green text
                    cloudBadgeLabel.setForeground(ModernTheme.COLOR_STATUS_OPEN);
                    // Light green background
                    cloudBadgeLabel.setBackground(ModernTheme.COLOR_STATUS_OPEN_BG);
                // If offline or local mode
                } else {
                    // Update text
                    cloudBadgeLabel.setText("  ☁ Local In-Memory Mode  ");
                    // Slate text
                    cloudBadgeLabel.setForeground(ModernTheme.COLOR_TEXT_MUTED);
                    // Light grey background
                    cloudBadgeLabel.setBackground(ModernTheme.COLOR_BG_INPUT);
                // End if
                }
            // End lambda
            });
        // End thenAccept
        });
    // End of method
    }
// End of class
}
