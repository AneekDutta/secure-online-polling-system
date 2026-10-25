// Package declaration for test suite
package com.polling.test;

// Import exceptions
import com.polling.exception.DuplicateVoteException;
// Import invalid vote exception
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
// Import Voter
import com.polling.model.Voter;
// Import services
import com.polling.service.PollManager;
// Import ResultAnalyzer
import com.polling.service.ResultAnalyzer;
// Import PollResultSummary
import com.polling.service.PollResultSummary;
// Import VotingService
import com.polling.service.VotingService;
// Import Arrays
import java.util.Arrays;
// Import List
import java.util.List;

// Standalone automated test runner verifying all OOP logic, duplicate checks, and analytics
public class VotingSystemTest {

    // Main entry point for executing automated tests from command line
    public static void main(String[] args) {
        // Print test execution header
        System.out.println("==================================================");
        // Print test suite name
        System.out.println("  RUNNING AUTOMATED OOP & VOTING SYSTEM TEST SUITE");
        // Print separator
        System.out.println("==================================================");

        // Counter for passed tests
        int passed = 0;
        // Counter for total tests
        int total = 0;

        // Execute test 1: Polymorphism and Inheritance
        total++;
        // Run test
        if (testInheritanceAndPolymorphism()) {
            // Increment passed
            passed++;
            // Print success
            System.out.println("[PASS] Test 1: Inheritance and Polymorphism verified.");
        // If failed
        } else {
            // Print failure
            System.err.println("[FAIL] Test 1: Inheritance and Polymorphism failed.");
        // End if
        }

        // Execute test 2: Successful Vote Recording
        total++;
        // Run test
        if (testSuccessfulVote()) {
            // Increment passed
            passed++;
            // Print success
            System.out.println("[PASS] Test 2: Successful Vote Recording verified.");
        // If failed
        } else {
            // Print failure
            System.err.println("[FAIL] Test 2: Successful Vote Recording failed.");
        // End if
        }

        // Execute test 3: Duplicate Vote Prevention via HashSet
        total++;
        // Run test
        if (testDuplicateVotePrevention()) {
            // Increment passed
            passed++;
            // Print success
            System.out.println("[PASS] Test 3: Duplicate Vote Prevention (HashSet) verified.");
        // If failed
        } else {
            // Print failure
            System.err.println("[FAIL] Test 3: Duplicate Vote Prevention failed.");
        // End if
        }

        // Execute test 4: Closed Poll Rejection
        total++;
        // Run test
        if (testClosedPollRejection()) {
            // Increment passed
            passed++;
            // Print success
            System.out.println("[PASS] Test 4: Closed Poll Rejection verified.");
        // If failed
        } else {
            // Print failure
            System.err.println("[FAIL] Test 4: Closed Poll Rejection failed.");
        // End if
        }

        // Execute test 5: Input Validations
        total++;
        // Run test
        if (testInputValidations()) {
            // Increment passed
            passed++;
            // Print success
            System.out.println("[PASS] Test 5: Input Validations verified.");
        // If failed
        } else {
            // Print failure
            System.err.println("[FAIL] Test 5: Input Validations failed.");
        // End if
        }

        // Execute test 6: Result Calculation, Percentages, and Tie Detection
        total++;
        // Run test
        if (testResultCalculationAndTies()) {
            // Increment passed
            passed++;
            // Print success
            System.out.println("[PASS] Test 6: Result Calculation & Tie Handling verified.");
        // If failed
        } else {
            // Print failure
            System.err.println("[FAIL] Test 6: Result Calculation & Tie Handling failed.");
        // End if
        }

        // Print test summary divider
        System.out.println("==================================================");
        // Print score
        System.out.println("  SUMMARY: " + passed + " / " + total + " TESTS PASSED SUCCESSFULLY.");
        // Print footer
        System.out.println("==================================================");

        // Exit with non-zero status if any test failed
        if (passed != total) {
            // Exit error code
            System.exit(1);
        // End if
        }
    // End of main
    }

    // Verifies User abstraction, Admin/Voter inheritance, and polymorphic methods
    private static boolean testInheritanceAndPolymorphism() {
        // Polymorphic reference for Admin
        User adminUser = new Admin("ADM-01", "Gauri Admin", "Dean Office");
        // Polymorphic reference for Voter
        User voterUser = new Voter("VTR-01", "Aneek Voter");
        // Verify admin role string
        if (!"ADMINISTRATOR".equals(adminUser.getRole())) return false;
        // Verify voter role string
        if (!"VOTER".equals(voterUser.getRole())) return false;
        // Verify admin permissions flag
        if (!adminUser.canManagePolls()) return false;
        // Verify voter permissions flag
        if (voterUser.canManagePolls()) return false;
        // Test passed
        return true;
    // End of method
    }

    // Verifies successful ballot submission and tallies
    private static boolean testSuccessfulVote() {
        // Try block for checked exceptions
        try {
            // Instantiate poll manager
            PollManager pm = new PollManager();
            // Instantiate voting service
            VotingService vs = new VotingService(pm);
            // Create a fresh test poll
            Poll poll = pm.createPoll("Sample Test Poll", Arrays.asList("Option Alpha", "Option Beta"));
            // Cast first vote
            vs.castVote(poll.getPollId(), "VOTER-NEW-1", "Option Alpha");
            // Check vote count
            if (poll.getVoteCountForOption("Option Alpha") != 1) return false;
            // Check total votes
            if (poll.getTotalVotes() != 1) return false;
            // Check hasVoted status in HashSet
            if (!poll.hasVoted("VOTER-NEW-1")) return false;
            // All assertions held
            return true;
        // Catch unexpected exceptions
        } catch (Exception e) {
            // Print stack trace
            e.printStackTrace();
            // Test failed
            return false;
        // End try-catch
        }
    // End of method
    }

    // Verifies that a duplicate vote by the same voter ID is rejected with DuplicateVoteException
    private static boolean testDuplicateVotePrevention() {
        // Instantiate poll manager
        PollManager pm = new PollManager();
        // Instantiate voting service
        VotingService vs = new VotingService(pm);
        // Try-catch block
        try {
            // Create fresh poll
            Poll poll = pm.createPoll("Duplicate Prevention Test", Arrays.asList("Yes", "No"));
            // Cast first valid vote
            vs.castVote(poll.getPollId(), "STUDENT-99", "Yes");
            // Attempt second vote with same voter ID (should throw DuplicateVoteException)
            vs.castVote(poll.getPollId(), "STUDENT-99", "No");
            // If execution reached here, duplicate check failed!
            return false;
        // Expected exception
        } catch (DuplicateVoteException dve) {
            // Expected duplicate exception successfully caught
            return true;
        // Catch any other unexpected exception
        } catch (Exception e) {
            // Wrong exception thrown
            return false;
        // End try-catch
        }
    // End of method
    }

    // Verifies that closing a poll prevents subsequent votes with PollClosedException
    private static boolean testClosedPollRejection() {
        // Instantiate poll manager
        PollManager pm = new PollManager();
        // Instantiate voting service
        VotingService vs = new VotingService(pm);
        // Instantiate admin user
        Admin admin = new Admin("ADMIN-X", "Head Proctor");
        // Try-catch block
        try {
            // Create test poll
            Poll poll = pm.createPoll("Closed Poll Test", Arrays.asList("Choice 1", "Choice 2"));
            // Cast vote while open
            vs.castVote(poll.getPollId(), "VOTER-A", "Choice 1");
            // Admin closes poll
            pm.closePoll(poll.getPollId(), admin);
            // Verify poll status is closed
            if (poll.isOpen()) return false;
            // Attempt to vote on closed poll (must throw PollClosedException)
            vs.castVote(poll.getPollId(), "VOTER-B", "Choice 2");
            // If reached here, closed poll allowed a vote!
            return false;
        // Expected exception
        } catch (PollClosedException pce) {
            // Successfully blocked vote on closed poll
            return true;
        // Catch unexpected exception
        } catch (Exception e) {
            // Wrong exception
            return false;
        // End try-catch
        }
    // End of method
    }

    // Verifies boundary validations: empty ID, invalid options, poll creation rules
    private static boolean testInputValidations() {
        // Instantiate poll manager
        PollManager pm = new PollManager();
        // Instantiate voting service
        VotingService vs = new VotingService(pm);
        // Try-catch block
        try {
            // 1. Verify empty question throws ValidationException
            boolean emptyQuestionCaught = false;
            // Try creating invalid poll
            try {
                // Empty question
                pm.createPoll("   ", Arrays.asList("A", "B"));
            // Catch expected validation exception
            } catch (ValidationException ve) {
                // Flag caught
                emptyQuestionCaught = true;
            // End try-catch
            }
            // Fail if not caught
            if (!emptyQuestionCaught) return false;

            // 2. Verify fewer than 2 options throws ValidationException
            boolean singleOptionCaught = false;
            // Try creating single option poll
            try {
                // Only one option provided
                pm.createPoll("Valid Question?", Arrays.asList("Solo Option"));
            // Catch expected validation exception
            } catch (ValidationException ve) {
                // Flag caught
                singleOptionCaught = true;
            // End try-catch
            }
            // Fail if not caught
            if (!singleOptionCaught) return false;

            // Create valid poll for voter validation checks
            Poll p = pm.createPoll("Validation Target", Arrays.asList("Apples", "Oranges"));

            // 3. Verify blank voter ID throws ValidationException
            boolean blankVoterCaught = false;
            // Try casting vote with empty ID
            try {
                // Blank voter ID
                vs.castVote(p.getPollId(), "  ", "Apples");
            // Catch expected validation exception
            } catch (ValidationException ve) {
                // Flag caught
                blankVoterCaught = true;
            // End try-catch
            }
            // Fail if not caught
            if (!blankVoterCaught) return false;

            // 4. Verify unlisted option throws InvalidVoteException
            boolean invalidOptionCaught = false;
            // Try casting vote with unlisted option
            try {
                // "Bananas" is not in ["Apples", "Oranges"]
                vs.castVote(p.getPollId(), "VALID-ID-1", "Bananas");
            // Catch expected invalid vote exception
            } catch (InvalidVoteException ive) {
                // Flag caught
                invalidOptionCaught = true;
            // End try-catch
            }
            // Fail if not caught
            if (!invalidOptionCaught) return false;

            // All validation tests passed
            return true;
        // Catch unexpected failure
        } catch (Exception e) {
            // Print error
            e.printStackTrace();
            // Test failed
            return false;
        // End try-catch
        }
    // End of method
    }

    // Verifies ResultAnalyzer computation, percentage precision, and tie scenario
    private static boolean testResultCalculationAndTies() {
        // Try-catch block
        try {
            // Instantiate poll manager
            PollManager pm = new PollManager();
            // Instantiate voting service
            VotingService vs = new VotingService(pm);
            // Instantiate result analyzer
            ResultAnalyzer analyzer = new ResultAnalyzer();

            // Create test poll
            Poll poll = pm.createPoll("Tie & Math Test", Arrays.asList("Red", "Blue", "Green"));

            // Zero-vote state test
            PollResultSummary zeroSummary = analyzer.analyze(poll);
            // Check zero votes
            if (zeroSummary.getTotalVotes() != 0) return false;
            // Check winning options is empty
            if (!zeroSummary.getWinningOptions().isEmpty()) return false;
            // Check percentage is 0.0
            if (zeroSummary.getOptionPercentages().get("Red") != 0.0) return false;

            // Cast 2 votes for Red and 2 votes for Blue (creates 50% - 50% tie)
            vs.castVote(poll.getPollId(), "USER-1", "Red");
            // Vote 2
            vs.castVote(poll.getPollId(), "USER-2", "Red");
            // Vote 3
            vs.castVote(poll.getPollId(), "USER-3", "Blue");
            // Vote 4
            vs.castVote(poll.getPollId(), "USER-4", "Blue");

            // Analyze poll
            PollResultSummary tieSummary = analyzer.analyze(poll);
            // Check total votes equals 4
            if (tieSummary.getTotalVotes() != 4) return false;
            // Check Red percentage is 50.0
            if (tieSummary.getOptionPercentages().get("Red") != 50.0) return false;
            // Check Blue percentage is 50.0
            if (tieSummary.getOptionPercentages().get("Blue") != 50.0) return false;
            // Check Green percentage is 0.0
            if (tieSummary.getOptionPercentages().get("Green") != 0.0) return false;
            // Check tie flag is true
            if (!tieSummary.isTie()) return false;
            // Check that winning options contains both Red and Blue
            List<String> winners = tieSummary.getWinningOptions();
            // Verify size is 2
            if (winners.size() != 2) return false;
            // Verify Red is present
            if (!winners.contains("Red")) return false;
            // Verify Blue is present
            if (!winners.contains("Blue")) return false;

            // Now break the tie with 1 more vote for Red
            vs.castVote(poll.getPollId(), "USER-5", "Red");
            // Re-analyze
            PollResultSummary winnerSummary = analyzer.analyze(poll);
            // Check total is 5
            if (winnerSummary.getTotalVotes() != 5) return false;
            // Check tie flag is now false
            if (winnerSummary.isTie()) return false;
            // Check winner is Red (3 out of 5 = 60.0%)
            if (!"Red".equals(winnerSummary.getWinningOptions().get(0))) return false;
            // Check Red percentage is 60.0
            if (winnerSummary.getOptionPercentages().get("Red") != 60.0) return false;

            // All math and tie conditions verified
            return true;
        // Catch failure
        } catch (Exception e) {
            // Print error
            e.printStackTrace();
            // Test failed
            return false;
        // End try-catch
        }
    // End of method
    }
// End of class
}
