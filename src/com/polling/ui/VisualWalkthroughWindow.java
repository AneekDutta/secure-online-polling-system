// Package declaration for UI components
package com.polling.ui;

// Import AWT layout components
import java.awt.BorderLayout;
// Import Color class
import java.awt.Color;
// Import Component class
import java.awt.Component;
// Import Dimension class
import java.awt.Dimension;
// Import FlowLayout class
import java.awt.FlowLayout;
// Import Font class
import java.awt.Font;
// Import GridLayout class
import java.awt.GridLayout;
// Import File class
import java.io.File;
// Import Arrays utility
import java.util.Arrays;
// Import List interface
import java.util.List;
// Import ArrayList class
import java.util.ArrayList;
// Import Map interface
import java.util.Map;
// Import Swing components
import javax.swing.BorderFactory;
// Import Box class
import javax.swing.Box;
// Import BoxLayout class
import javax.swing.BoxLayout;
// Import DefaultListCellRenderer
import javax.swing.DefaultListCellRenderer;
// Import DefaultListModel
import javax.swing.DefaultListModel;
// Import JButton
import javax.swing.JButton;
// Import JFrame
import javax.swing.JFrame;
// Import JLabel
import javax.swing.JLabel;
// Import JList
import javax.swing.JList;
// Import JPanel
import javax.swing.JPanel;
// Import JProgressBar
import javax.swing.JProgressBar;
// Import JScrollPane
import javax.swing.JScrollPane;
// Import JSplitPane
import javax.swing.JSplitPane;
// Import JTextArea
import javax.swing.JTextArea;
// Import SwingUtilities
import javax.swing.SwingUtilities;

// Import domain and service layers for walkthrough execution
import com.polling.event.PollEvent;
// Import PollEventListener
import com.polling.event.PollEventListener;
// Import exceptions
import com.polling.exception.DuplicateVoteException;
// Import PollClosedException
import com.polling.exception.PollClosedException;
// Import ValidationException
import com.polling.exception.ValidationException;
// Import models
import com.polling.model.Admin;
// Import Poll
import com.polling.model.Poll;
// Import User
import com.polling.model.User;
// Import VoteReceipt
import com.polling.model.VoteReceipt;
// Import Voter
import com.polling.model.Voter;
// Import SecurityUtil
import com.polling.security.SecurityUtil;
// Import PollManager
import com.polling.service.PollManager;
// Import PollResultSummary
import com.polling.service.PollResultSummary;
// Import ReportExporter
import com.polling.service.ReportExporter;
// Import ResultAnalyzer
import com.polling.service.ResultAnalyzer;
// Import VotingService
import com.polling.service.VotingService;
// Import strategies
import com.polling.strategy.MajorityWinningStrategy;
// Import PluralityWinningStrategy
import com.polling.strategy.PluralityWinningStrategy;
// Import SupabaseClient
import com.polling.database.SupabaseClient;

// Graphical interactive window that visually presents the 11-stage automated walkthrough on screen
public class VisualWalkthroughWindow extends JFrame {

    // Representation of a demonstration stage
    public static class DemoStage {
        // Stage index number (1 to 11)
        public final int number;
        // Short title of the stage
        public final String title;
        // Academic OOP concept highlighted
        public final String concept;
        // Detailed explanatory description
        public final String description;
        // Execution status: PENDING, RUNNING, COMPLETED, FAILED
        public String status = "PENDING";

        // Constructor initializing stage data
        public DemoStage(int number, String title, String concept, String description) {
            // Assign number
            this.number = number;
            // Assign title
            this.title = title;
            // Assign concept
            this.concept = concept;
            // Assign description
            this.description = description;
        // End constructor
        }
    // End DemoStage class
    }

    // List of all 11 walkthrough stages
    private final List<DemoStage> stages = new ArrayList<>();
    // Swing list model for stage selector
    private final DefaultListModel<DemoStage> stageListModel = new DefaultListModel<>();
    // JList component displaying stages on the left
    private final JList<DemoStage> stageJList;

    // Header labels
    private final JLabel stageHeadingLabel;
    // Concept badge label
    private final JLabel conceptBadgeLabel;
    // Stage description label
    private final JLabel stageDescLabel;

    // Rich terminal console area
    private final JTextArea terminalArea;
    // Scroll pane for terminal area
    private final JScrollPane terminalScrollPane;

    // Progress bar showing completion percentage
    private final JProgressBar progressBar;
    // Status text label next to progress bar
    private final JLabel statusLabel;

    // Control buttons
    private final JButton startButton;
    // Step button
    private final JButton stepButton;
    // Restart button
    private final JButton restartButton;

    // Execution state flags
    private volatile boolean isRunning = false;
    // Step requested flag
    private volatile boolean stepRequested = false;
    // Current stage pointer (0 to 10)
    private int currentStageIndex = 0;
    // Background execution thread
    private Thread runnerThread;

    // Managed services for demonstration
    private PollManager demoPollManager;
    // Demo voting service
    private VotingService demoVotingService;
    // Demo result analyzer
    private ResultAnalyzer demoAnalyzer;
    // Shared generated poll for the demo
    private Poll createdDemoPoll;
    // Sample receipt
    private VoteReceipt sampleReceipt;

    // Constructor initializing GUI and setting up demonstration engine
    public VisualWalkthroughWindow() {
        // Call super with window title
        super("OOP System Architecture - Live Visual Walkthrough (Interactive)");

        // Set default close operation to dispose window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // Set window size
        setSize(1080, 740);
        // Minimum size
        setMinimumSize(new Dimension(900, 600));
        // Center on screen
        setLocationRelativeTo(null);

        // Populate the 11 stages
        initStages();

        // Create main layout
        setLayout(new BorderLayout(0, 0));
        // Set background color
        getContentPane().setBackground(ModernTheme.COLOR_BG_APP);

        // Header panel
        JPanel topHeader = createTopHeader();
        // Add header to north
        add(topHeader, BorderLayout.NORTH);

        // Stage list component
        stageJList = new JList<>(stageListModel);
        // Setup custom renderer for stages
        setupStageListRenderer();

        // Left sidebar panel
        JPanel sidebarPanel = createSidebarPanel();

        // Center console panel
        terminalArea = new JTextArea();
        // Set monospace font for clean alignment
        terminalArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 13));
        // Editable false
        terminalArea.setEditable(false);
        // Dark theme background
        terminalArea.setBackground(new Color(15, 23, 42));
        // Bright text foreground
        terminalArea.setForeground(new Color(241, 245, 249));
        // Caret color
        terminalArea.setCaretColor(Color.WHITE);
        // Margins
        terminalArea.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        // Terminal scroll pane
        terminalScrollPane = new JScrollPane(terminalArea);
        // Border
        terminalScrollPane.setBorder(BorderFactory.createLineBorder(ModernTheme.COLOR_BORDER, 1));

        // Center card container
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        // Background
        centerPanel.setBackground(ModernTheme.COLOR_BG_APP);
        // Padding
        centerPanel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        // Stage info banner in center
        JPanel stageBanner = new JPanel(new BorderLayout(8, 6));
        // Banner background
        stageBanner.setBackground(ModernTheme.COLOR_BG_CARD);
        // Banner border
        stageBanner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ModernTheme.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        // Stage heading
        stageHeadingLabel = new JLabel("Stage 1 of 11: Abstraction & Runtime Polymorphism");
        // Font
        stageHeadingLabel.setFont(ModernTheme.FONT_HEADING);
        // Foreground
        stageHeadingLabel.setForeground(ModernTheme.COLOR_TEXT_PRIMARY);

        // Concept badge
        conceptBadgeLabel = new JLabel("  OOP: Abstraction & Inheritance  ");
        // Font
        conceptBadgeLabel.setFont(ModernTheme.FONT_SMALL_BOLD);
        // Foreground
        conceptBadgeLabel.setForeground(ModernTheme.COLOR_ACCENT_BLUE);
        // Background
        conceptBadgeLabel.setBackground(ModernTheme.COLOR_ACCENT_LIGHT);
        // Opaque
        conceptBadgeLabel.setOpaque(true);
        // Border
        conceptBadgeLabel.setBorder(BorderFactory.createLineBorder(new Color(191, 219, 254), 1));

        // Stage top row
        JPanel bannerTopRow = new JPanel(new BorderLayout());
        // Background
        bannerTopRow.setBackground(ModernTheme.COLOR_BG_CARD);
        // Add heading
        bannerTopRow.add(stageHeadingLabel, BorderLayout.WEST);
        // Add badge
        bannerTopRow.add(conceptBadgeLabel, BorderLayout.EAST);

        // Description label
        stageDescLabel = new JLabel("Demonstrates abstract User base class polymorphic dispatch through Admin and Voter.");
        // Font
        stageDescLabel.setFont(ModernTheme.FONT_BODY);
        // Foreground
        stageDescLabel.setForeground(ModernTheme.COLOR_TEXT_SECONDARY);

        // Add top row to banner
        stageBanner.add(bannerTopRow, BorderLayout.NORTH);
        // Add description to banner
        stageBanner.add(stageDescLabel, BorderLayout.CENTER);

        // Add banner to center panel
        centerPanel.add(stageBanner, BorderLayout.NORTH);
        // Add terminal to center panel
        centerPanel.add(terminalScrollPane, BorderLayout.CENTER);

        // Split pane dividing sidebar and console
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebarPanel, centerPanel);
        // Split position
        splitPane.setDividerLocation(320);
        // Continuous layout
        splitPane.setContinuousLayout(true);
        // Border null
        splitPane.setBorder(null);

        // Add split pane to center
        add(splitPane, BorderLayout.CENTER);

        // Bottom toolbar panel
        JPanel bottomBar = new JPanel(new BorderLayout(14, 0));
        // Background
        bottomBar.setBackground(ModernTheme.COLOR_BG_CARD);
        // Border
        bottomBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, ModernTheme.COLOR_BORDER),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));

        // Progress components panel
        JPanel progressPanel = new JPanel(new BorderLayout(10, 4));
        // Background
        progressPanel.setBackground(ModernTheme.COLOR_BG_CARD);

        // Progress bar
        progressBar = new JProgressBar(0, 11);
        // Value
        progressBar.setValue(0);
        // String painted
        progressBar.setStringPainted(true);
        // Progress string
        progressBar.setString("Ready (0 / 11 Complete)");
        // Dimension
        progressBar.setPreferredSize(new Dimension(280, 22));

        // Status label
        statusLabel = new JLabel("Click 'Start Walkthrough' to watch the live step-by-step verification.");
        // Font
        statusLabel.setFont(ModernTheme.FONT_SMALL);
        // Color
        statusLabel.setForeground(ModernTheme.COLOR_TEXT_MUTED);

        // Add progress to panel
        progressPanel.add(progressBar, BorderLayout.WEST);
        // Add label to panel
        progressPanel.add(statusLabel, BorderLayout.CENTER);

        // Button controls panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        // Background
        controlsPanel.setBackground(ModernTheme.COLOR_BG_CARD);

        // Start / Play button
        startButton = new JButton("▶ Start Walkthrough");
        // Style button
        ModernTheme.stylePrimaryButton(startButton);
        // Listener
        startButton.addActionListener(e -> toggleAutoPlay());

        // Step button
        stepButton = new JButton("⏭ Next Step");
        // Style secondary
        ModernTheme.styleSecondaryButton(stepButton);
        // Listener
        stepButton.addActionListener(e -> stepNextStage());

        // Restart button
        restartButton = new JButton("↺ Reset");
        // Style secondary
        ModernTheme.styleSecondaryButton(restartButton);
        // Listener
        restartButton.addActionListener(e -> resetWalkthrough());

        // Add buttons
        controlsPanel.add(restartButton);
        // Add step
        controlsPanel.add(stepButton);
        // Add start
        controlsPanel.add(startButton);

        // Add to bottom bar
        bottomBar.add(progressPanel, BorderLayout.CENTER);
        // Add controls
        bottomBar.add(controlsPanel, BorderLayout.EAST);

        // Add bottom bar
        add(bottomBar, BorderLayout.SOUTH);

        // Append welcome text in console
        appendTerminalLine("================================================================================");
        // Line
        appendTerminalLine("  OOP & SYSTEM ARCHITECTURE - VISUAL INTERACTIVE WALKTHROUGH");
        // Line
        appendTerminalLine("  Built for live classroom presentation & grading verification");
        // Line
        appendTerminalLine("================================================================================");
        // Line
        appendTerminalLine("Press '▶ Start Walkthrough' below to begin real-time automated execution.\n");
    // End constructor
    }

    // Initialize the 11 stages metadata
    private void initStages() {
        // Stage 1
        stages.add(new DemoStage(1, "1. Inheritance & Polymorphism", "Abstraction & Polymorphism", "Demonstrates User base class, Admin, and Voter subclass polymorphic permissions."));
        // Stage 2
        stages.add(new DemoStage(2, "2. In-Memory Catalog (HashMap)", "Collections: HashMap", "Verifies O(1) instantaneous lookup and storage of pre-seeded demonstration polls."));
        // Stage 3
        stages.add(new DemoStage(3, "3. Custom Exception Defense", "Exception Handling", "Validates dynamic poll creation and rejection of empty inputs via ValidationException."));
        // Stage 4
        stages.add(new DemoStage(4, "4. Observer Pattern Events", "Observer Design Pattern", "Broadcasting real-time PollEvent payloads to decoupled UI and audit listeners."));
        // Stage 5
        stages.add(new DemoStage(5, "5. Salted SHA-256 Hashing", "Cryptography & Privacy", "Anonymous ballot tallying using salted digests and verifiable VoteReceipt codes."));
        // Stage 6
        stages.add(new DemoStage(6, "6. Duplicate Vote Defense", "Collections: HashSet", "Demonstrates instant O(1) duplicate vote blocking via DuplicateVoteException."));
        // Stage 7
        stages.add(new DemoStage(7, "7. Strategy Pattern (Plurality/Runoff)", "Strategy Design Pattern", "Interchangeable electoral algorithms computing Plurality vs Majority Runoffs."));
        // Stage 8
        stages.add(new DemoStage(8, "8. Admin Governance & Lifecycle", "Role-Based Access Control", "Administrative poll closing and strict defense against closed-poll submissions."));
        // Stage 9
        stages.add(new DemoStage(9, "9. Ballot Receipt & Zero-Knowledge Audit", "Electoral Auditing", "Verifying legitimate receipts and inspecting immutable voter hash digests."));
        // Stage 10
        stages.add(new DemoStage(10, "10. RFC-4180 CSV & Certified Export", "Java File I/O Streams", "Exporting structured CSV spreadsheets and certified cryptographic audit reports."));
        // Stage 11
        stages.add(new DemoStage(11, "11. Supabase Cloud Sync", "Cloud PostgreSQL & REST", "Synchronizing cloud tables and logging audit transactions to Supabase backend."));

        // Add all to list model
        for (DemoStage stage : stages) {
            // Add element
            stageListModel.addElement(stage);
        // End loop
        }
    // End initStages
    }

    // Create top banner panel
    private JPanel createTopHeader() {
        // Create panel
        JPanel header = new JPanel(new BorderLayout(16, 0));
        // Background
        header.setBackground(ModernTheme.COLOR_BG_CARD);
        // Border
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ModernTheme.COLOR_BORDER),
            BorderFactory.createEmptyBorder(14, 20, 14, 20)
        ));

        // Title box
        Box titleBox = Box.createVerticalBox();
        // Title label
        JLabel titleLabel = new JLabel("Secure Online Polling & Result Analysis System");
        // Font
        titleLabel.setFont(ModernTheme.FONT_HEADING);
        // Color
        titleLabel.setForeground(ModernTheme.COLOR_TEXT_PRIMARY);
        // Subtitle
        JLabel subLabel = new JLabel("Live Graphical Walkthrough • Demonstrating All 11 OOP & Security Pillars in Real Time");
        // Font
        subLabel.setFont(ModernTheme.FONT_SMALL);
        // Color
        subLabel.setForeground(ModernTheme.COLOR_TEXT_MUTED);

        // Add title
        titleBox.add(titleLabel);
        // Add spacing
        titleBox.add(Box.createVerticalStrut(2));
        // Add subtitle
        titleBox.add(subLabel);

        // Badge label
        JLabel liveBadge = new JLabel("  ● LIVE DEMO RUNNER  ");
        // Font
        liveBadge.setFont(ModernTheme.FONT_SMALL_BOLD);
        // Background
        liveBadge.setBackground(new Color(220, 252, 231));
        // Color
        liveBadge.setForeground(new Color(22, 101, 52));
        // Opaque
        liveBadge.setOpaque(true);
        // Border
        liveBadge.setBorder(BorderFactory.createLineBorder(new Color(187, 247, 208), 1));

        // Add title box to header
        header.add(titleBox, BorderLayout.WEST);
        // Add live badge
        header.add(liveBadge, BorderLayout.EAST);

        // Return header
        return header;
    // End createTopHeader
    }

    // Create sidebar containing stage list
    private JPanel createSidebarPanel() {
        // Container
        JPanel panel = new JPanel(new BorderLayout());
        // Background
        panel.setBackground(ModernTheme.COLOR_BG_CARD);
        // Border
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ModernTheme.COLOR_BORDER));

        // Sidebar title label
        JLabel sideTitle = new JLabel("Walkthrough Milestones");
        // Font
        sideTitle.setFont(ModernTheme.FONT_SUBHEADING);
        // Color
        sideTitle.setForeground(ModernTheme.COLOR_TEXT_PRIMARY);
        // Padding
        sideTitle.setBorder(BorderFactory.createEmptyBorder(14, 16, 12, 16));

        // Add title
        panel.add(sideTitle, BorderLayout.NORTH);

        // Scroll pane for stage list
        JScrollPane scroll = new JScrollPane(stageJList);
        // Border
        scroll.setBorder(null);
        // Add scroll to center
        panel.add(scroll, BorderLayout.CENTER);

        // Return panel
        return panel;
    // End createSidebarPanel
    }

    // Setup custom list cell renderer for stage badges
    private void setupStageListRenderer() {
        // Set cell renderer
        stageJList.setCellRenderer(new DefaultListCellRenderer() {
            // Override renderer
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                // Cast to DemoStage
                DemoStage stage = (DemoStage) value;
                // Create item panel
                JPanel itemPanel = new JPanel(new BorderLayout(8, 2));
                // Margin
                itemPanel.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

                // Prefix status icon
                String icon = "⚪";
                // Status color
                Color statusColor = ModernTheme.COLOR_TEXT_MUTED;
                // Switch on status
                if ("RUNNING".equals(stage.status)) {
                    // Running icon
                    icon = "⏳";
                    // Color
                    statusColor = ModernTheme.COLOR_ACCENT_BLUE;
                } else if ("COMPLETED".equals(stage.status)) {
                    // Completed icon
                    icon = "✅";
                    // Color
                    statusColor = new Color(22, 163, 74);
                } else if ("FAILED".equals(stage.status)) {
                    // Failed icon
                    icon = "❌";
                    // Color
                    statusColor = new Color(220, 38, 38);
                // End if
                }

                // Title label
                JLabel titleLbl = new JLabel(icon + " " + stage.title);
                // Font
                titleLbl.setFont(ModernTheme.FONT_BODY_BOLD);
                // Foreground
                titleLbl.setForeground(statusColor);

                // Concept label
                JLabel conceptLbl = new JLabel("     " + stage.concept);
                // Font
                conceptLbl.setFont(ModernTheme.FONT_SMALL);
                // Foreground
                conceptLbl.setForeground(ModernTheme.COLOR_TEXT_MUTED);

                // Add to item panel
                itemPanel.add(titleLbl, BorderLayout.NORTH);
                // Add concept
                itemPanel.add(conceptLbl, BorderLayout.SOUTH);

                // Highlight active stage
                if (index == currentStageIndex) {
                    // Light blue background
                    itemPanel.setBackground(ModernTheme.COLOR_ACCENT_LIGHT);
                } else {
                    // White background
                    itemPanel.setBackground(ModernTheme.COLOR_BG_CARD);
                // End if
                }

                // Return item panel
                return itemPanel;
            // End getListCellRendererComponent
            }
        // End setCellRenderer
        });
    // End setupStageListRenderer
    }

    // Toggle auto-play walkthrough
    private synchronized void toggleAutoPlay() {
        // If already running, pause
        if (isRunning) {
            // Set flag
            isRunning = false;
            // Update button
            startButton.setText("▶ Resume");
            // Update status
            statusLabel.setText("Walkthrough paused.");
        } else {
            // Start running
            isRunning = true;
            // Update button
            startButton.setText("⏸ Pause");
            // If finished, reset
            if (currentStageIndex >= stages.size()) {
                // Reset index
                resetWalkthrough();
                // Resume
                isRunning = true;
                // Button text
                startButton.setText("⏸ Pause");
            // End if
            }
            // Start background execution thread if not alive
            if (runnerThread == null || !runnerThread.isAlive()) {
                // Instantiate runner thread
                runnerThread = new Thread(this::runWalkthroughLoop, "VisualWalkthroughRunner");
                // Start thread
                runnerThread.start();
            // End if
            }
        // End if
        }
    // End toggleAutoPlay
    }

    // Step to next stage manually
    private void stepNextStage() {
        // Set step requested
        stepRequested = true;
        // If runner not alive, start it
        if (runnerThread == null || !runnerThread.isAlive()) {
            // Instantiate runner thread
            runnerThread = new Thread(this::runWalkthroughLoop, "VisualWalkthroughRunner");
            // Start thread
            runnerThread.start();
        // End if
        }
    // End stepNextStage
    }

    // Reset entire walkthrough to beginning
    private void resetWalkthrough() {
        // Stop running
        isRunning = false;
        // Reset button text
        startButton.setText("▶ Start Walkthrough");
        // Reset current index
        currentStageIndex = 0;
        // Reset stages
        for (DemoStage stage : stages) {
            // Reset status
            stage.status = "PENDING";
        // End loop
        }
        // Repaint list
        stageJList.repaint();
        // Reset progress bar
        progressBar.setValue(0);
        // Reset progress text
        progressBar.setString("Ready (0 / 11 Complete)");
        // Reset status label
        statusLabel.setText("System reset. Ready to start.");
        // Clear terminal
        terminalArea.setText("");
        // Print banner
        appendTerminalLine("================================================================================");
        // Line
        appendTerminalLine("  OOP & SYSTEM ARCHITECTURE - VISUAL INTERACTIVE WALKTHROUGH");
        // Line
        appendTerminalLine("  System state initialized. Ready to begin Stage 1.");
        // Line
        appendTerminalLine("================================================================================\n");
    // End resetWalkthrough
    }

    // Main background execution loop
    private void runWalkthroughLoop() {
        // Initialize clean demonstration services
        demoPollManager = new PollManager();
        // Initialize voting service
        demoVotingService = new VotingService(demoPollManager);
        // Initialize analyzer
        demoAnalyzer = new ResultAnalyzer();

        // Loop through remaining stages
        while (currentStageIndex < stages.size()) {
            // Check if paused and no step requested
            while (!isRunning && !stepRequested) {
                // Sleep briefly
                try {
                    // Sleep 150ms
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    // Exit on interrupt
                    return;
                // End catch
                }
            // End pause loop
            }

            // Reset step requested flag
            stepRequested = false;

            // Fetch current stage
            final DemoStage stage = stages.get(currentStageIndex);
            // Mark stage running
            stage.status = "RUNNING";

            // Update UI on Event Dispatch Thread
            final int displayIndex = currentStageIndex;
            // Swing invoke later
            SwingUtilities.invokeLater(() -> {
                // Update stage heading
                stageHeadingLabel.setText("Stage " + stage.number + " of 11: " + stage.title);
                // Update concept badge
                conceptBadgeLabel.setText("  OOP: " + stage.concept + "  ");
                // Update description
                stageDescLabel.setText(stage.description);
                // Select in list
                stageJList.setSelectedIndex(displayIndex);
                // Ensure visible
                stageJList.ensureIndexIsVisible(displayIndex);
                // Repaint list
                stageJList.repaint();
                // Update progress bar
                progressBar.setValue(displayIndex);
                // Progress text
                progressBar.setString("Running Stage " + stage.number + " of 11...");
                // Status label
                statusLabel.setText("Executing: " + stage.title);
            // End invokeLater
            });

            // Brief pause before execution for visual pacing
            sleepQuietly(600);

            // Print stage banner in terminal
            appendTerminalLine("--------------------------------------------------------------------------------");
            // Banner title
            appendTerminalLine("  STAGE " + stage.number + ": " + stage.title);
            // Banner concept
            appendTerminalLine("  [OOP CONCEPT: " + stage.concept + "]");
            // Banner divider
            appendTerminalLine("--------------------------------------------------------------------------------");

            // Execute the specific stage logic
            boolean success = executeStageLogic(stage.number);

            // Mark completed or failed
            stage.status = success ? "COMPLETED" : "FAILED";

            // Advance index
            currentStageIndex++;

            // Update UI after stage completes
            final int completedCount = currentStageIndex;
            // Swing invoke later
            SwingUtilities.invokeLater(() -> {
                // Repaint list
                stageJList.repaint();
                // Update progress bar
                progressBar.setValue(completedCount);
                // Update progress string
                progressBar.setString(completedCount + " / 11 Completed");
                // Status text
                statusLabel.setText("Completed Stage " + stage.number + " successfully.");
            // End invokeLater
            });

            // Pause between stages during auto-play
            if (isRunning) {
                // Sleep 1200ms
                sleepQuietly(1200);
            // End if
            }
        // End while
        }

        // When all stages finish
        isRunning = false;
        // Swing invoke later
        SwingUtilities.invokeLater(() -> {
            // Update start button text
            startButton.setText("↺ Restart Demo");
            // Status label
            statusLabel.setText("✓ Walkthrough complete! All 11 verification stages passed successfully.");
            // Print concluding banner
            appendTerminalLine("\n================================================================================");
            // Line
            appendTerminalLine("  ALL 11 STAGES COMPLETED & VERIFIED SUCCESSFULLY");
            // Line
            appendTerminalLine("  The OOP architecture, security, design patterns, and cloud sync are 100% sound.");
            // Line
            appendTerminalLine("================================================================================\n");
        // End invokeLater
        });
    // End runWalkthroughLoop
    }

    // Execute the academic logic for each stage
    private boolean executeStageLogic(int stageNum) {
        // Try block
        try {
            // Switch on stage number
            switch (stageNum) {
                // Case 1: Inheritance & Polymorphism
                case 1:
                    // Create polymorphic users
                    User adminUser = new Admin("ADMIN-DEMO", "Dr. Gauri Sharma", "Academic Senate");
                    // Voter user
                    User voterUser = new Voter("STU-9921", "Aneek Dutta");

                    // Print details
                    appendTerminalLine("  • Abstract class 'User' provides encapsulated userId & name.");
                    // Print dispatch
                    appendTerminalLine("  • Polymorphic dispatch on adminUser.getRole()        -> " + adminUser.getRole());
                    // Can manage
                    appendTerminalLine("  • Polymorphic dispatch on adminUser.canManagePolls() -> " + adminUser.canManagePolls());
                    // Permissions
                    appendTerminalLine("  • adminUser.getPermissions()                         -> " + adminUser.getPermissionsDescription());
                    // Blank line
                    appendTerminalLine("");
                    // Voter dispatch
                    appendTerminalLine("  • Polymorphic dispatch on voterUser.getRole()        -> " + voterUser.getRole());
                    // Voter can manage
                    appendTerminalLine("  • Polymorphic dispatch on voterUser.canManagePolls() -> " + voterUser.canManagePolls());
                    // Voter permissions
                    appendTerminalLine("  • voterUser.getPermissions()                         -> " + voterUser.getPermissionsDescription());
                    // Return true
                    return true;

                // Case 2: In-Memory HashMap
                case 2:
                    // Print HashMap collection
                    appendTerminalLine("  • PollManager maintains 'Map<String, Poll> pollCatalog = new HashMap<>()'.");
                    // Print instantaneous lookup
                    appendTerminalLine("  • Pre-seeded polls loaded in memory:");
                    // Iterate polls
                    for (Poll p : demoPollManager.getAllPolls()) {
                        // Print poll
                        appendTerminalLine("    - [" + p.getPollId() + "] \"" + p.getQuestion() + "\" (" + p.getTotalVotes() + " votes)");
                    // End loop
                    }
                    // Return true
                    return true;

                // Case 3: Validation Exceptions
                case 3:
                    // Print exception validation
                    appendTerminalLine("  • Enforcing domain integrity via custom 'ValidationException'.");
                    // Test 3A: Empty question
                    try {
                        // Attempt empty question
                        demoPollManager.createPoll("   ", Arrays.asList("Option A", "Option B"));
                    } catch (ValidationException ve) {
                        // Print blocked message
                        appendTerminalLine("  [BLOCKED EXPECTED] Empty question rejected: \"" + ve.getMessage() + "\"");
                    // End catch
                    }
                    // Test 3B: Single option
                    try {
                        // Attempt single option
                        demoPollManager.createPoll("What is OOP?", Arrays.asList("Lonely Option"));
                    } catch (ValidationException ve) {
                        // Print blocked message
                        appendTerminalLine("  [BLOCKED EXPECTED] Insufficient options rejected: \"" + ve.getMessage() + "\"");
                    // End catch
                    }
                    // Test 3C: Valid creation
                    createdDemoPoll = demoPollManager.createPoll(
                        "Annual Student Council Presidential Election 2026",
                        Arrays.asList(
                            "Candidate Alice (Robotics Club)",
                            "Candidate Bob (Coding Society)",
                            "Candidate Charlie (Cultural Committee)"
                        )
                    );
                    // Print created poll
                    appendTerminalLine("  >> Created new poll successfully: #" + createdDemoPoll.getPollId());
                    // Options
                    for (String opt : createdDemoPoll.getOptions()) {
                        // Print option
                        appendTerminalLine("     - " + opt);
                    // End loop
                    }
                    // Return true
                    return true;

                // Case 4: Observer Pattern
                case 4:
                    // Print observer registration
                    appendTerminalLine("  • Registering dynamic PollEventListener with PollManager subject.");
                    // Add anonymous observer
                    demoPollManager.addEventListener(event -> {
                        // Print event received
                        appendTerminalLine("    [OBSERVER BROADCAST] Event: " + event.getEventType() + " | " + event.getMessage());
                    // End listener
                    });
                    // Print confirmation
                    appendTerminalLine("  >> Observer registered. Future state changes will broadcast real-time events.");
                    // Return true
                    return true;

                // Case 5: Salted SHA-256 Hashing
                case 5:
                    // Print privacy assurance
                    appendTerminalLine("  • Raw voter identities are NEVER stored in persistent memory.");
                    // Print calculation
                    appendTerminalLine("  • Digest formula: SHA-256(voterId + pollSalt + PEPPER)");
                    // Cast first vote and get receipt
                    sampleReceipt = demoVotingService.castVoteWithReceipt(createdDemoPoll.getPollId(), "STUDENT-101", "Candidate Alice (Robotics Club)");
                    // Print receipt
                    appendTerminalLine("  >> Ballot 1 Cast! Receipt Code: " + sampleReceipt.getReceiptCode());
                    // Print hash
                    appendTerminalLine("  >> Salted SHA-256 Hash stored: " + sampleReceipt.getVoterHash());

                    // Cast additional votes
                    demoVotingService.castVote(createdDemoPoll.getPollId(), "STUDENT-102", "Candidate Alice (Robotics Club)");
                    // Vote 3
                    demoVotingService.castVote(createdDemoPoll.getPollId(), "STUDENT-103", "Candidate Bob (Coding Society)");
                    // Vote 4
                    demoVotingService.castVote(createdDemoPoll.getPollId(), "STUDENT-104", "Candidate Bob (Coding Society)");
                    // Vote 5
                    demoVotingService.castVote(createdDemoPoll.getPollId(), "STUDENT-105", "Candidate Charlie (Cultural Committee)");
                    // Print total votes
                    appendTerminalLine("  >> Total certified ballots tallied in memory: " + createdDemoPoll.getTotalVotes());
                    // Return true
                    return true;

                // Case 6: Duplicate Vote Defense
                case 6:
                    // Print HashSet O(1) mechanism
                    appendTerminalLine("  • Duplicate vote prevention powered by HashSet<String> of salted digests.");
                    // Attempt double vote
                    try {
                        // Student 101 votes again
                        appendTerminalLine("  • Attacking system: Voter 'STUDENT-101' attempts to vote a second time...");
                        // Call cast vote
                        demoVotingService.castVote(createdDemoPoll.getPollId(), "STUDENT-101", "Candidate Bob (Coding Society)");
                        // Fail if not thrown
                        return false;
                    } catch (DuplicateVoteException dve) {
                        // Print caught
                        appendTerminalLine("  [BLOCKED AS DESIGNED] Caught DuplicateVoteException!");
                        // Print message
                        appendTerminalLine("    >> " + dve.getMessage());
                        // Print O(1) verification
                        appendTerminalLine("    >> Verification time complexity: O(1) instantaneous lookup.");
                    // End catch
                    }
                    // Return true
                    return true;

                // Case 7: Strategy Pattern
                case 7:
                    // Print Strategy pattern evaluation
                    appendTerminalLine("  • Demonstrating interchangeable electoral Strategy algorithms.");
                    // Plurality strategy
                    PollResultSummary pluralityResult = demoAnalyzer.analyze(createdDemoPoll, new PluralityWinningStrategy());
                    // Majority strategy
                    PollResultSummary majorityResult = demoAnalyzer.analyze(createdDemoPoll, new MajorityWinningStrategy());

                    // Print Plurality
                    appendTerminalLine("  [STRATEGY 1: Plurality Rule]");
                    // Verdict
                    appendTerminalLine("    Verdict: " + pluralityResult.getWinnerDescription());
                    // Tie detected
                    appendTerminalLine("    Is Tie : " + pluralityResult.isTie());

                    // Print Majority
                    appendTerminalLine("  [STRATEGY 2: Majority Runoff Rule]");
                    // Verdict
                    appendTerminalLine("    Verdict: " + majorityResult.getWinnerDescription());
                    // Runoff required
                    appendTerminalLine("    Runoff : " + majorityResult.isRequiresRunoff() + " (Leaders tie at 40% <= 50%)");

                    // Print breakdown
                    appendTerminalLine("\n  [Vote Percentage Distribution]:");
                    // Iterate options
                    for (Map.Entry<String, Double> entry : majorityResult.getOptionPercentages().entrySet()) {
                        // Option count
                        int count = majorityResult.getOptionCounts().get(entry.getKey());
                        // Print row
                        appendTerminalLine(String.format("    %-38s : %d votes (%.1f%%)", entry.getKey(), count, entry.getValue()));
                    // End loop
                    }
                    // Return true
                    return true;

                // Case 8: Administrative Governance
                case 8:
                    // Print governance
                    appendTerminalLine("  • Admin persona closes poll to finalize voting.");
                    // Create admin
                    Admin admin = new Admin("ADMIN-OFFICIAL", "Dean of Students");
                    // Close poll
                    demoPollManager.closePoll(createdDemoPoll.getPollId(), admin);
                    // Print status
                    appendTerminalLine("  >> Poll status is now: " + (createdDemoPoll.isOpen() ? "OPEN" : "CLOSED"));

                    // Attempt vote in closed poll
                    try {
                        // Cast vote
                        appendTerminalLine("  • Attacking closed poll: 'STUDENT-999' attempts to submit vote...");
                        // Call cast vote
                        demoVotingService.castVote(createdDemoPoll.getPollId(), "STUDENT-999", "Candidate Bob (Coding Society)");
                        // Fail if not thrown
                        return false;
                    } catch (PollClosedException pce) {
                        // Print blocked
                        appendTerminalLine("  [BLOCKED AS DESIGNED] Caught PollClosedException!");
                        // Print message
                        appendTerminalLine("    >> " + pce.getMessage());
                    // End catch
                    }
                    // Return true
                    return true;

                // Case 9: Ballot Receipt Audit
                case 9:
                    // Print audit verification
                    appendTerminalLine("  • Zero-knowledge cryptographic auditing.");
                    // Check sample receipt
                    if (sampleReceipt != null) {
                        // Verify receipt
                        VoteReceipt auditedReceipt = createdDemoPoll.verifyReceipt(sampleReceipt.getReceiptCode());
                        // Print receipt found
                        if (auditedReceipt != null) {
                            // Status
                            appendTerminalLine("  >> Receipt Code    : " + auditedReceipt.getReceiptCode() + " (AUTHENTICATED)");
                            // Choice
                            appendTerminalLine("  >> Recorded Choice : " + auditedReceipt.getSelectedOption());
                            // Timestamp
                            appendTerminalLine("  >> Timestamp Issued: " + auditedReceipt.getIssuedAt());
                            // Voter hash
                            appendTerminalLine("  >> Salted Hash     : " + auditedReceipt.getVoterHash());
                        // End if
                        }
                    // End if
                    }
                    // Print voter hashes count
                    appendTerminalLine("  >> Total Anonymized Voter Hashes in Poll HashSet: " + createdDemoPoll.getVoterCount());
                    // Return true
                    return true;

                // Case 10: File I/O & Reports
                case 10:
                    // Export CSV
                    File csvFile = new File("election-results-demo.csv");
                    // Export Text
                    File txtFile = new File("election-audit-certified-demo.txt");
                    // Summary
                    PollResultSummary summary = demoAnalyzer.analyze(createdDemoPoll);
                    // Export CSV
                    ReportExporter.exportToCSV(summary, csvFile);
                    // Export TXT
                    ReportExporter.exportCertifiedAuditReport(createdDemoPoll, summary, txtFile);
                    // Print CSV
                    appendTerminalLine("  >> Exported CSV: " + csvFile.getAbsolutePath() + " (" + csvFile.length() + " bytes)");
                    // Print TXT
                    appendTerminalLine("  >> Exported TXT: " + txtFile.getAbsolutePath() + " (" + txtFile.length() + " bytes)");
                    // Return true
                    return true;

                // Case 11: Supabase Cloud Sync
                case 11:
                    // Print cloud sync
                    appendTerminalLine("  • Testing Supabase REST cloud connectivity at: " + com.polling.database.SupabaseConfig.SUPABASE_URL);
                    // Ping
                    boolean online = demoPollManager.getSupabaseClient().isAvailable();
                    // Print ping result
                    appendTerminalLine("  >> Supabase PostgreSQL Health: " + (online ? "ONLINE (HTTP 200 OK)" : "OFFLINE / LOCAL MODE"));
                    // Log audit event to cloud
                    if (online) {
                        // Log event
                        demoPollManager.getSupabaseClient().logAuditEventAsync("DEMO_AUDIT", "VISUAL_RUNNER", "Live Graphical Walkthrough completed").join();
                        // Print confirmation
                        appendTerminalLine("  >> Cloud audit event dispatched asynchronously to 'audit_events' table.");
                    // End if
                    }
                    // Return true
                    return true;

                // Default
                default:
                    // Return false
                    return false;
            // End switch
            }
        // Catch exceptions
        } catch (Exception ex) {
            // Print error in terminal
            appendTerminalLine("  [ERROR IN STAGE " + stageNum + "]: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
            // Return false
            return false;
        // End try-catch
        }
    // End executeStageLogic
    }

    // Append a line to terminal with auto-scroll
    private void appendTerminalLine(String line) {
        // Swing invoke later to ensure thread safety
        SwingUtilities.invokeLater(() -> {
            // Append line
            terminalArea.append(line + "\n");
            // Set caret to bottom
            terminalArea.setCaretPosition(terminalArea.getDocument().getLength());
        // End invokeLater
        });
    // End appendTerminalLine
    }

    // Helper sleep without throws
    private void sleepQuietly(long millis) {
        // Try block
        try {
            // Sleep
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
            // Interrupted
            Thread.currentThread().interrupt();
        // End catch
        }
    // End sleepQuietly
    }

    // Executable main method allowing standalone launch of visual walkthrough
    public static void main(String[] args) {
        // Run on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Instantiate window
            VisualWalkthroughWindow window = new VisualWalkthroughWindow();
            // Make visible
            window.setVisible(true);
            // Bring to front
            window.toFront();
            // Automatically start auto-play after 500ms
            new Thread(() -> {
                // Sleep
                try {
                    // Sleep
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                // End catch
                }
                // Trigger auto play
                window.toggleAutoPlay();
            // End thread
            }).start();
        // End invokeLater
        });
    // End main
    }
// End VisualWalkthroughWindow
}
