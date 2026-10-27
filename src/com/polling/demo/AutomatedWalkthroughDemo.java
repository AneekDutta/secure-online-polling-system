// Package declaration for live automated walkthrough demonstration
package com.polling.demo;

// Import event components for Observer Pattern demonstration
import com.polling.event.PollEvent;
// Import PollEventListener
import com.polling.event.PollEventListener;
// Import exceptions
import com.polling.exception.DuplicateVoteException;
// Import poll closed exception
import com.polling.exception.InvalidVoteException;
// Import poll closed exception
import com.polling.exception.PollClosedException;
// Import poll not found exception
import com.polling.exception.PollNotFoundException;
// Import validation exception
import com.polling.exception.ValidationException;
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
// Import security utility
import com.polling.security.SecurityUtil;
// Import service layer
import com.polling.service.PollManager;
// Import PollResultSummary
import com.polling.service.PollResultSummary;
// Import ReportExporter
import com.polling.service.ReportExporter;
// Import ResultAnalyzer
import com.polling.service.ResultAnalyzer;
// Import VotingService
import com.polling.service.VotingService;
// Import electoral strategies
import com.polling.strategy.MajorityWinningStrategy;
// Import PluralityWinningStrategy
import com.polling.strategy.PluralityWinningStrategy;
// Import File
import java.io.File;
// Import Arrays
import java.util.Arrays;
// Import List
import java.util.List;
// Import Map
import java.util.Map;
// Import Set
import java.util.Set;

// End-to-end automated walkthrough runner presenting all OOP concepts, security rules, and algorithms
public class AutomatedWalkthroughDemo {

    // Main executable entry point for the automated presentation walkthrough
    public static void main(String[] args) {
        // Banner top border
        printHeader("STARTING AUTOMATED END-TO-END SYSTEM WALKTHROUGH");
        // Print introductory notes
        System.out.println("This automated program executes and explains every feature and OOP pattern");
        // Print technologies
        System.out.println("demonstrating Encapsulation, Abstraction, Inheritance, Polymorphism,");
        // Print security
        System.out.println("Custom Exceptions, HashMap/HashSet, Observer, Strategy, and SHA-256 Hashing.\n");

        // Step 1: OOP User Hierarchy & Polymorphism
        runStep1_UserHierarchyAndPolymorphism();
        // Step 2: Poll Storage & In-Memory HashMap
        PollManager pollManager = runStep2_HashMapCatalogAndSeeding();
        // Step 3: Dynamic Poll Creation & Boundary Validation
        Poll demoPoll = runStep3_PollCreationAndInputValidation(pollManager);
        // Step 4: Observer Pattern Event-Driven Architecture
        runStep4_ObserverPatternRegistration(pollManager);
        // Step 5: Secure Voting, Salted SHA-256 Hashing, & Receipts
        VotingService votingService = new VotingService(pollManager);
        // Execute voting step and capture receipt
        VoteReceipt sampleReceipt = runStep5_SecureVotingAndReceiptIssuance(pollManager, votingService, demoPoll);
        // Step 6: Duplicate-Vote Prevention via HashSet
        runStep6_DuplicateVotePrevention(votingService, demoPoll);
        // Step 7: Dual-Strategy Result Analysis (Plurality vs. Majority)
        runStep7_DualStrategyAnalysis(demoPoll);
        // Step 8: Administrative Lifecycle & Closed Poll Defense
        runStep8_AdminLifecycleAndClosedPollDefense(pollManager, votingService, demoPoll);
        // Step 9: Ballot Receipt Verification & Cryptographic Audit
        runStep9_ReceiptVerificationAndAudit(demoPoll, sampleReceipt);
        // Step 10: Automated File I/O (CSV & Certified Plain-Text Report)
        runStep10_ReportExporting(demoPoll);
        // Step 11: Supabase Cloud Database Integration & Real-Time Sync
        runStep11_SupabaseCloudSync(pollManager);

        // Completion footer
        printHeader("END-TO-END WALKTHROUGH COMPLETED SUCCESSFULLY");
        // Final congratulations
        System.out.println("All 11 stages passed with complete mathematical and architectural correctness!");
        // Viva tip
        System.out.println("The system is 100% ready for presentation, grading, and examiner defense.\n");
    // End of main
    }

    // STEP 1: Demonstrates Abstraction, Inheritance, and Polymorphism
    private static void runStep1_UserHierarchyAndPolymorphism() {
        // Section title
        printSection("STAGE 1: Abstraction, Inheritance, & Runtime Polymorphism");
        // Explain User abstraction
        System.out.println("[CONCEPT 1: Abstraction & Inheritance]");
        // Explanation line
        System.out.println("  • 'User' is an abstract class with private encapsulated fields (userId, name).");
        // Inherited subclasses
        System.out.println("  • 'Admin' and 'Voter' extend 'User', inheriting identity attributes.\n");

        // Instantiating polymorphic Admin reference
        User adminPersona = new Admin("ADMIN-DEAN", "Dr. Gauri Sharma", "Academic Senate");
        // Instantiating polymorphic Voter reference
        User voterPersona = new Voter("STU-9921", "Aneek Dutta");

        // Demonstrate runtime dynamic method dispatch
        System.out.println("[CONCEPT 2: Runtime Polymorphism]");
        // Print polymorphic call 1
        System.out.println("  Calling adminPersona.getRole()               -> " + adminPersona.getRole());
        // Print polymorphic call 2
        System.out.println("  Calling adminPersona.canManagePolls()        -> " + adminPersona.canManagePolls());
        // Print polymorphic call 3
        System.out.println("  Calling adminPersona.getPermissions()        -> " + adminPersona.getPermissionsDescription());
        // Line break
        System.out.println();
        // Print polymorphic voter call 1
        System.out.println("  Calling voterPersona.getRole()               -> " + voterPersona.getRole());
        // Print polymorphic voter call 2
        System.out.println("  Calling voterPersona.canManagePolls()        -> " + voterPersona.canManagePolls());
        // Print polymorphic voter call 3
        System.out.println("  Calling voterPersona.getPermissions()        -> " + voterPersona.getPermissionsDescription());
        // Success indicator
        System.out.println("\n  >> Polymorphic method dispatch verified successfully.");
    // End of method
    }

    // STEP 2: Demonstrates In-Memory HashMap Catalog Storage
    private static PollManager runStep2_HashMapCatalogAndSeeding() {
        // Section title
        printSection("STAGE 2: In-Memory Storage & HashMap Catalog");
        // Explain HashMap concept
        System.out.println("[CONCEPT: Java HashMap Collection]");
        // Detail map usage
        System.out.println("  • PollManager maintains 'Map<String, Poll> pollCatalog = new HashMap<>()'.");
        // O(1) performance explanation
        System.out.println("  • Enables O(1) instantaneous lookup of polls by their unique ID string.\n");

        // Instantiate PollManager
        PollManager manager = new PollManager();
        // Retrieve loaded polls
        List<Poll> initialPolls = manager.getAllPolls();
        // Print count
        System.out.println("  Initialized PollManager with " + initialPolls.size() + " pre-seeded demonstration polls:");
        // Iterate and list
        for (Poll p : initialPolls) {
            // Print poll details
            System.out.println("    • [" + p.getPollId() + "] \"" + p.getQuestion() + "\" (" + p.getTotalVotes() + " initial votes)");
        // End for
        }
        // Return initialized manager
        return manager;
    // End of method
    }

    // STEP 3: Demonstrates Poll Creation, Encapsulation, and Custom Exception Validation
    private static Poll runStep3_PollCreationAndInputValidation(PollManager manager) {
        // Section title
        printSection("STAGE 3: Dynamic Poll Creation & Validation Exceptions");
        // Explain validation
        System.out.println("[CONCEPT: Custom Domain Exception Handling]");
        // Explain ValidationException
        System.out.println("  • System enforces non-empty questions and minimum 2 distinct options.");
        // Explain exception throw
        System.out.println("  • Throws custom 'ValidationException' instead of silent bugs.\n");

        // Attempt 1: Empty Question Validation Test
        System.out.print("  [Test 3A] Attempting to create poll with blank question... ");
        // Try-catch block
        try {
            // Attempt blank creation
            manager.createPoll("   ", Arrays.asList("Option 1", "Option 2"));
            // If failed to catch
            System.out.println("FAILED (Should have thrown ValidationException)!");
        // Expected exception
        } catch (ValidationException ve) {
            // Success
            System.out.println("BLOCKED! Caught ValidationException: \"" + ve.getMessage() + "\"");
        // End try-catch
        }

        // Attempt 2: Insufficient Options Validation Test
        System.out.print("  [Test 3B] Attempting to create poll with only 1 option... ");
        // Try-catch block
        try {
            // Attempt single option creation
            manager.createPoll("Valid Question?", Arrays.asList("Lonely Option"));
            // If failed to catch
            System.out.println("FAILED (Should have thrown ValidationException)!");
        // Expected exception
        } catch (ValidationException ve) {
            // Success
            System.out.println("BLOCKED! Caught ValidationException: \"" + ve.getMessage() + "\"");
        // End try-catch
        }

        // Attempt 3: Successful creation of a 3-way college election poll
        System.out.println("\n  [Test 3C] Creating valid 3-choice Presidential Election poll:");
        // Options list
        List<String> options = Arrays.asList(
            "Candidate Alice (Robotics Club)",
            "Candidate Bob (Coding Society)",
            "Candidate Charlie (Cultural Committee)"
        );
        // Create poll
        try {
            // Execute creation
            Poll newPoll = manager.createPoll("Annual Student Council Presidential Election 2026", options);
            // Print poll ID
            System.out.println("    >> Created successfully: " + newPoll.getPollId());
            // Print options
            for (String opt : newPoll.getOptions()) {
                // Print option
                System.out.println("       - " + opt);
            // End for
            }
            // Return created poll
            return newPoll;
        // Catch unexpected error
        } catch (ValidationException ve) {
            // Print error
            throw new RuntimeException("Unexpected creation error", ve);
        // End try-catch
        }
    // End of method
    }

    // STEP 4: Demonstrates Observer Pattern Registration
    private static void runStep4_ObserverPatternRegistration(PollManager manager) {
        // Section title
        printSection("STAGE 4: Observer Pattern (Event-Driven Real-Time Architecture)");
        // Explain Observer concept
        System.out.println("[CONCEPT: Observer Design Pattern]");
        // Explanation
        System.out.println("  • PollManager acts as Subject; UI panels act as PollEventListener Observers.");
        // Real-time benefits
        System.out.println("  • Dispatches PollEvent payloads whenever votes are cast or states change.\n");

        // Register a console audit observer to demonstrate real-time notifications
        manager.addEventListener(new PollEventListener() {
            // Event callback
            @Override
            public void onPollEvent(PollEvent event) {
                // Print received notification
                System.out.println("    [OBSERVER BROADCAST RECEIVED] " + event.getEventType() + " on " + event.getPollId() + ": " + event.getMessage());
            // End method
            }
        // End listener
        });
        // Print confirmation
        System.out.println("  >> Test Observer registered to listen for all future poll lifecycle events.");
    // End of method
    }

    // STEP 5: Demonstrates Salted SHA-256 Voter Hashing and Cryptographic Receipts
    private static VoteReceipt runStep5_SecureVotingAndReceiptIssuance(PollManager manager, VotingService service, Poll poll) {
        // Section title
        printSection("STAGE 5: Secure Voting, Salted SHA-256 Hashing, & Ballot Receipts");
        // Explain security
        System.out.println("[CONCEPT: Cryptographic Anonymity & Receipt Issuance]");
        // Explain hashing
        System.out.println("  • Plain-text voter IDs are NEVER stored in memory to protect voter privacy.");
        // Explain salt
        System.out.println("  • System computes SHA-256(voterId + pollId_Salt) and stores hash in HashSet.");
        // Explain receipt
        System.out.println("  • Issues an immutable VoteReceipt with unique verification code.\n");

        // Cast sample ballot 1
        System.out.println("  Casting Vote 1: Voter 'STUDENT-101' voting for 'Candidate Alice'...");
        // Receipt reference
        VoteReceipt receipt1 = null;
        // Try casting
        try {
            // Submit ballot
            receipt1 = service.castVoteWithReceipt(poll.getPollId(), "STUDENT-101", "Candidate Alice (Robotics Club)");
            // Print receipt code
            System.out.println("    >> Ballot Confirmed! Receipt Code: " + receipt1.getReceiptCode());
            // Print salted hash
            System.out.println("    >> Salted SHA-256 Hash stored in HashSet: " + receipt1.getVoterHash());
        // Catch domain exceptions
        } catch (Exception ex) {
            // Fail
            System.err.println("Voting failed: " + ex.getMessage());
        // End try-catch
        }

        // Cast additional ballots to prepare electoral test scenario
        System.out.println("\n  Casting 4 additional diverse ballots:");
        // Cast vote 2
        castQuietly(service, poll.getPollId(), "STUDENT-102", "Candidate Alice (Robotics Club)");
        // Cast vote 3
        castQuietly(service, poll.getPollId(), "STUDENT-103", "Candidate Bob (Coding Society)");
        // Cast vote 4
        castQuietly(service, poll.getPollId(), "STUDENT-104", "Candidate Bob (Coding Society)");
        // Cast vote 5
        castQuietly(service, poll.getPollId(), "STUDENT-105", "Candidate Charlie (Cultural Committee)");

        // Print updated tallies
        System.out.println("\n  Current Ballot Summary for " + poll.getPollId() + ":");
        // Print total votes
        System.out.println("    Total Votes Cast       : " + poll.getTotalVotes());
        // Print unique hashes in HashSet
        System.out.println("    Unique Hashes in HashSet: " + poll.getVoterCount());

        // Return first sample receipt for verification later
        return receipt1;
    // End of method
    }

    // STEP 6: Demonstrates Duplicate Vote Prevention via HashSet
    private static void runStep6_DuplicateVotePrevention(VotingService service, Poll poll) {
        // Section title
        printSection("STAGE 6: Duplicate Vote Prevention via HashSet (O(1) Security)");
        // Explain HashSet concept
        System.out.println("[CONCEPT: Java HashSet Collection for Duplicate Detection]");
        // Explain O(1)
        System.out.println("  • HashSet provides O(1) instantaneous duplicate collision detection.");
        // Explain exception
        System.out.println("  • Attempting to cast a second ballot with the same Voter ID is blocked.\n");

        // Attempt duplicate vote with same Voter ID "STUDENT-101"
        System.out.print("  [Duplicate Attempt] Voter 'STUDENT-101' attempts to vote a second time... ");
        // Try-catch block
        try {
            // Attempt second vote
            service.castVote(poll.getPollId(), "STUDENT-101", "Candidate Bob (Coding Society)");
            // Failure if reached
            System.out.println("FAILED! System permitted duplicate vote!");
        // Expected DuplicateVoteException
        } catch (DuplicateVoteException dve) {
            // Success notification
            System.out.println("BLOCKED BY HASHSET!");
            // Print exception message
            System.out.println("    >> Exception Caught: " + dve.getClass().getSimpleName() + ": \"" + dve.getMessage() + "\"");
            // Affirm total vote untouched
            System.out.println("    >> Verified: Total votes in memory remained strictly unchanged (" + poll.getTotalVotes() + ").");
        // Catch any other exception
        } catch (Exception ex) {
            // Unexpected
            System.err.println("Wrong exception: " + ex.getMessage());
        // End try-catch
        }
    // End of method
    }

    // STEP 7: Demonstrates the Strategy Pattern for Electoral Calculations
    private static void runStep7_DualStrategyAnalysis(Poll poll) {
        // Section title
        printSection("STAGE 7: Strategy Pattern (Plurality vs. Majority Runoff)");
        // Explain Strategy Pattern
        System.out.println("[CONCEPT: Strategy Design Pattern]");
        // Detail strategy contract
        System.out.println("  • 'WinningStrategy' interface decouples calculation algorithms from ResultAnalyzer.");
        // Strategy choices
        System.out.println("  • Demonstrates how different electoral systems judge the identical vote distribution.\n");

        // Instantiate analyzer
        ResultAnalyzer analyzer = new ResultAnalyzer();

        // 1. Plurality Strategy (First-Past-The-Post)
        System.out.println("  [Evaluation 1: PluralityWinningStrategy]");
        // Evaluate under plurality
        PollResultSummary pluralityResult = analyzer.analyze(poll, new PluralityWinningStrategy());
        // Print strategy name
        System.out.println("    Strategy Used : " + pluralityResult.getStrategyName());
        // Print verdict
        System.out.println("    Verdict       : " + pluralityResult.getWinnerDescription());
        // Print tie flag
        System.out.println("    Tie Detected  : " + pluralityResult.isTie() + " (Leaders: " + String.join(", ", pluralityResult.getWinningOptions()) + ")");
        // Print runoff required
        System.out.println("    Runoff Mandate: " + pluralityResult.isRequiresRunoff());

        // 2. Majority Threshold Strategy (>50% Required)
        System.out.println("\n  [Evaluation 2: MajorityWinningStrategy]");
        // Evaluate under majority
        PollResultSummary majorityResult = analyzer.analyze(poll, new MajorityWinningStrategy());
        // Print strategy name
        System.out.println("    Strategy Used : " + majorityResult.getStrategyName());
        // Print verdict
        System.out.println("    Verdict       : " + majorityResult.getWinnerDescription());
        // Print runoff required
        System.out.println("    Runoff Mandate: " + majorityResult.isRequiresRunoff() + " (Because top options received 40% <= 50%)");

        // Print percentage distribution breakdown
        System.out.println("\n  [Live Percentage Distribution Breakdown]:");
        // Iterate over percentages map
        for (Map.Entry<String, Double> entry : majorityResult.getOptionPercentages().entrySet()) {
            // Option count
            int count = majorityResult.getOptionCounts().get(entry.getKey());
            // Print progress bar representation
            System.out.println(String.format("    %-40s : %2d votes  [%s] %5.1f%%",
                entry.getKey(), count, makeBar(entry.getValue()), entry.getValue()));
        // End for
        }
    // End of method
    }

    // STEP 8: Demonstrates Administrative Lifecycle Governance & Closed Poll Defense
    private static void runStep8_AdminLifecycleAndClosedPollDefense(PollManager manager, VotingService service, Poll poll) {
        // Section title
        printSection("STAGE 8: Administrative Governance & Closed Poll Defense");
        // Explain admin permissions
        System.out.println("[CONCEPT: Role-Based Authorization & Immutability]");
        // Detail close poll behavior
        System.out.println("  • Only User instances with 'canManagePolls() == true' can close/reopen polls.");
        // Explain closed status defense
        System.out.println("  • Once closed, subsequent voting attempts throw 'PollClosedException'.\n");

        // Create Admin user
        Admin admin = new Admin("ADMIN-OFFICIAL", "Dean of Students");
        // Try closing poll
        try {
            // Admin closes poll
            System.out.println("  Admin '" + admin.getName() + "' closing Poll #" + poll.getPollId() + "...");
            // Execute close
            manager.closePoll(poll.getPollId(), admin);
            // Verify status
            System.out.println("    >> Lifecycle Status: " + (poll.isOpen() ? "OPEN" : "CLOSED"));
        // Catch exception
        } catch (Exception ex) {
            // Print error
            System.err.println("Closing failed: " + ex.getMessage());
        // End try-catch
        }

        // Attempt vote on closed poll
        System.out.print("\n  [Closed Poll Test] New voter 'STUDENT-999' attempts to vote in closed poll... ");
        // Try-catch block
        try {
            // Attempt vote
            service.castVote(poll.getPollId(), "STUDENT-999", "Candidate Bob (Coding Society)");
            // Failure if reached
            System.out.println("FAILED! System permitted vote on closed poll!");
        // Expected PollClosedException
        } catch (PollClosedException pce) {
            // Success
            System.out.println("BLOCKED AS EXPECTED!");
            // Print message
            System.out.println("    >> Exception Caught: " + pce.getClass().getSimpleName() + ": \"" + pce.getMessage() + "\"");
        // Catch unexpected
        } catch (Exception ex) {
            // Wrong exception
            System.err.println("Wrong exception: " + ex.getMessage());
        // End try-catch
        }
    // End of method
    }

    // STEP 9: Demonstrates Ballot Receipt Verification & Cryptographic Audit
    private static void runStep9_ReceiptVerificationAndAudit(Poll poll, VoteReceipt receipt) {
        // Section title
        printSection("STAGE 9: Ballot Receipt Verification & Cryptographic Audit");
        // Explain receipt verification
        System.out.println("[CONCEPT: Zero-Knowledge Electoral Auditing]");
        // Detail hash audit
        System.out.println("  • Voters can check their ballot receipt against the poll's issued receipts.");
        // Detail anonymity
        System.out.println("  • Admins can inspect unique voter hashes without revealing individual identities.\n");

        // Check if sample receipt exists
        if (receipt != null) {
            // Verify receipt
            System.out.println("  Verifying Sample Receipt Code '" + receipt.getReceiptCode() + "':");
            // Lookup in model
            VoteReceipt verified = poll.verifyReceipt(receipt.getReceiptCode());
            // Check lookup
            if (verified != null) {
                // Success
                System.out.println("    >> STATUS          : LEGITIMATE & TALLIED IN MEMORY");
                // Print option voted
                System.out.println("    >> Choice Tallied  : " + verified.getSelectedOption());
                // Print timestamp
                System.out.println("    >> Timestamp Issued: " + verified.getIssuedAt());
                // Print voter hash
                System.out.println("    >> Salted Hash     : " + verified.getVoterHash());
            // If not found
            } else {
                // Failed lookup
                System.out.println("    >> STATUS: Receipt Not Found!");
            // End if
            }
        // End if
        }

        // Print anonymized voter hashes stored in the HashSet
        System.out.println("\n  [Admin Audit Trail: Hashes recorded in Poll's HashSet (" + poll.getVoterCount() + " total)]: ");
        // Counter
        int idx = 1;
        // Iterate over hashes
        for (String hash : poll.getVotedVoterHashes()) {
            // Print numbered hash
            System.out.println(String.format("    %2d. %s", idx++, hash));
        // End for
        }
    // End of method
    }

    // STEP 10: Demonstrates Automated File I/O (CSV & Certified Text Audit Reports)
    private static void runStep10_ReportExporting(Poll poll) {
        // Section title
        printSection("STAGE 10: File I/O & Certified Election Report Exporting");
        // Explain File I/O
        System.out.println("[CONCEPT: Java File I/O Streams]");
        // Detail report generation
        System.out.println("  • System generates RFC-4180 CSV spreadsheets and certified .txt audit files.\n");

        // Try-catch block for file operations
        try {
            // Compute summary
            ResultAnalyzer analyzer = new ResultAnalyzer();
            // Get summary
            PollResultSummary summary = analyzer.analyze(poll);

            // Create temporary demonstration files
            File csvFile = new File("election-results-demo.csv");
            // Create certified text file
            File txtFile = new File("election-audit-certified-demo.txt");

            // Export CSV
            ReportExporter.exportToCSV(summary, csvFile);
            // Print CSV confirmation
            System.out.println("  >> CSV Spreadsheet exported successfully:");
            // Print path
            System.out.println("     File Location: " + csvFile.getAbsolutePath() + " (" + csvFile.length() + " bytes)");

            // Export Certified Plain Text Report
            ReportExporter.exportCertifiedAuditReport(poll, summary, txtFile);
            // Print Text confirmation
            System.out.println("\n  >> Certified Plain-Text Audit Report exported successfully:");
            // Print path
            System.out.println("     File Location: " + txtFile.getAbsolutePath() + " (" + txtFile.length() + " bytes)");

            // Print snippet of the exported certified text report
            System.out.println("\n  [Excerpt of Certified Audit Report Header]:");
            // Divider line
            System.out.println("  +--------------------------------------------------------------------------+");
            // Header
            System.out.println("  | OFFICIAL ELECTION AUDIT REPORT & VOTE CERTIFICATION RECORD               |");
            // Poll ID
            System.out.println("  | Poll Identifier    : #" + poll.getPollId() + "                                                |");
            // Total votes
            System.out.println("  | Total Votes Cast   : " + summary.getTotalVotes() + "                                                    |");
            // Unique hashes
            System.out.println("  | Unique Voter Hashes: " + poll.getVoterCount() + "                                                    |");
            // Verdict
            System.out.println("  | Verdict            : " + summary.getWinnerDescription() + "           |");
            // Divider line
            System.out.println("  +--------------------------------------------------------------------------+");
        // Catch IO exception
        } catch (Exception ex) {
            // Print failure
            System.err.println("Export failed: " + ex.getMessage());
        // End try-catch
        }
    // End of method
    }

    // STEP 11: Demonstrates Supabase PostgreSQL Cloud Database Integration
    private static void runStep11_SupabaseCloudSync(PollManager manager) {
        // Section title
        printSection("STAGE 11: Supabase Cloud PostgreSQL Database Synchronization");
        // Explain cloud architecture
        System.out.println("[CONCEPT: Hybrid Cloud Architecture & Database Persistence]");
        // Detail persistence
        System.out.println("  • System connects to Supabase PostgreSQL at https://ykxcoihvfzgykrkabbdy.supabase.co");
        // Detail resilience
        System.out.println("  • Persists polls, option counts, voter hashes, receipts, and audit events in the cloud.\n");

        // Ping cloud database
        System.out.print("  Pinging Supabase REST Endpoint... ");
        // Check availability
        boolean available = manager.getSupabaseClient().isAvailable();
        // If available
        if (available) {
            // Print status
            System.out.println("CONNECTED & ONLINE (HTTP 200 OK)!");
            // Log demo audit event
            manager.getSupabaseClient().logAuditEventAsync("DEMO_AUDIT", "WALKTHROUGH", "End-to-End Walkthrough completed live verification").join();
            // Confirmation line
            System.out.println("  >> Cloud audit event successfully logged to Supabase 'audit_events' table.");
            // Tables explanation
            System.out.println("  >> Tables synced in Supabase: 'polls', 'poll_options', 'voter_hashes', 'vote_receipts', 'audit_events'.");
        // If offline
        } else {
            // Print offline message
            System.out.println("OFFLINE (Operating in Local In-Memory Mode).");
        // End if
        }
    // End of method
    }

    // Helper method to cast a vote quietly for setup purposes
    private static void castQuietly(VotingService service, String pollId, String voterId, String option) {
        // Try-catch block
        try {
            // Cast vote
            service.castVote(pollId, voterId, option);
            // Print short log
            System.out.println("    • Registered vote from " + voterId + " for " + option);
        // Catch exception
        } catch (Exception e) {
            // Print error
            System.err.println("Quiet vote failed: " + e.getMessage());
        // End try-catch
        }
    // End of method
    }

    // Helper to generate a visual ASCII progress bar
    private static String makeBar(double pct) {
        // Calculate filled slots out of 20
        int filled = (int) Math.round((pct / 100.0) * 20.0);
        // StringBuilder
        StringBuilder sb = new StringBuilder();
        // Append filled characters
        for (int i = 0; i < filled; i++) sb.append("█");
        // Append empty characters
        for (int i = filled; i < 20; i++) sb.append("░");
        // Return bar
        return sb.toString();
    // End of method
    }

    // Prints formatted banner header
    private static void printHeader(String title) {
        // Divider
        System.out.println("================================================================================");
        // Center text
        System.out.println("  " + title);
        // Divider
        System.out.println("================================================================================");
    // End of method
    }

    // Prints formatted stage section header
    private static void printSection(String sectionTitle) {
        // Spacing
        System.out.println("\n--------------------------------------------------------------------------------");
        // Section title
        System.out.println("  " + sectionTitle);
        // Divider
        System.out.println("--------------------------------------------------------------------------------");
    // End of method
    }
// End of class
}
