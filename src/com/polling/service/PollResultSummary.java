//ROLE: Immutable Data Transfer Object (DTO) for Analytical Results
//WHAT IT DOES:-
//Encapsulates Snapshot Results: Holds a read-only snapshot of an analyzed poll at a specific point in time:
//                        pollId, question, and open status.
//                        totalVotes.
//                        optionCounts (Map<String, Integer>) wrapped in Collections.unmodifiableMap.
//                        optionPercentages (Map<String, Double>).
//                        winningOptions (List<String>) and a tie boolean flag.
//                        requiresRunoff and strategyName
//Human-Readable Verdict: Provides getWinnerDescription(), which produces clean text verdicts like 
//                        "Leading Option: Java", "Tie between: Java, Python", or "No votes recorded yet.".


// Package declaration for service layer components
package com.polling.service;

// Import Collections utility class
import java.util.Collections;
// Import List interface
import java.util.List;
// Import Map interface
import java.util.Map;

// Immutable Data Transfer Object summarizing computed analytical results of a poll
public class PollResultSummary {

    // Private field storing the poll identifier
    private final String pollId;

    // Private field storing the poll question
    private final String question;

    // Private field storing whether the poll was open when analyzed
    private final boolean open;

    // Private field storing the total vote tally
    private final int totalVotes;

    // Private field storing the counts for each candidate option
    private final Map<String, Integer> optionCounts;

    // Private field storing the calculated percentages for each option
    private final Map<String, Double> optionPercentages;

    // Private field storing the list of winning option titles (supports ties)
    private final List<String> winningOptions;

    // Private field indicating if there is a tie among leaders
    private final boolean tie;

    // Private field indicating if a runoff is required under majority rules
    private final boolean requiresRunoff;

    // Private field storing the electoral strategy name used
    private final String strategyName;

    // Private field storing a custom verdict explanation
    private final String customVerdict;

    // Backwards-compatible constructor defaulting to Plurality evaluation
    public PollResultSummary(String pollId,
                             String question,
                             boolean open,
                             int totalVotes,
                             Map<String, Integer> optionCounts,
                             Map<String, Double> optionPercentages,
                             List<String> winningOptions,
                             boolean tie) {
        // Delegate to comprehensive constructor with default plurality strategy
        this(pollId, question, open, totalVotes, optionCounts, optionPercentages, winningOptions, tie, false, "Plurality", null);
    // End of constructor
    }

    // Comprehensive constructor initializing all computed result metrics and strategy parameters
    public PollResultSummary(String pollId,
                             String question,
                             boolean open,
                             int totalVotes,
                             Map<String, Integer> optionCounts,
                             Map<String, Double> optionPercentages,
                             List<String> winningOptions,
                             boolean tie,
                             boolean requiresRunoff,
                             String strategyName,
                             String customVerdict) {
        // Assign the poll ID
        this.pollId = pollId;
        // Assign the question text
        this.question = question;
        // Assign the poll open status
        this.open = open;
        // Assign the total vote count
        this.totalVotes = totalVotes;
        // Assign the option counts map defensively
        this.optionCounts = (optionCounts != null) ? Collections.unmodifiableMap(optionCounts) : Collections.emptyMap();
        // Assign the option percentages map defensively
        this.optionPercentages = (optionPercentages != null) ? Collections.unmodifiableMap(optionPercentages) : Collections.emptyMap();
        // Assign the list of winning options defensively
        this.winningOptions = (winningOptions != null) ? Collections.unmodifiableList(winningOptions) : Collections.emptyList();
        // Assign the tie status flag
        this.tie = tie;
        // Assign runoff flag
        this.requiresRunoff = requiresRunoff;
        // Assign strategy name
        this.strategyName = (strategyName != null) ? strategyName : "Plurality";
        // Assign custom verdict
        this.customVerdict = customVerdict;
    // End of constructor
    }

    // Getter for poll ID
    public String getPollId() {
        // Return poll ID
        return this.pollId;
    // End of method
    }

    // Getter for question
    public String getQuestion() {
        // Return question text
        return this.question;
    // End of method
    }

    // Getter for poll open state
    public boolean isOpen() {
        // Return open state boolean
        return this.open;
    // End of method
    }

    // Getter for total votes
    public int getTotalVotes() {
        // Return total votes
        return this.totalVotes;
    // End of method
    }

    // Getter for option counts
    public Map<String, Integer> getOptionCounts() {
        // Return option counts map
        return this.optionCounts;
    // End of method
    }

    // Getter for option percentages
    public Map<String, Double> getOptionPercentages() {
        // Return option percentages map
        return this.optionPercentages;
    // End of method
    }

    // Getter for winning options
    public List<String> getWinningOptions() {
        // Return list of winning options
        return this.winningOptions;
    // End of method
    }

    // Getter for tie status
    public boolean isTie() {
        // Return tie flag
        return this.tie;
    // End of method
    }

    // Getter for runoff requirement flag
    public boolean isRequiresRunoff() {
        // Return runoff flag
        return this.requiresRunoff;
    // End of method
    }

    // Getter for strategy name
    public String getStrategyName() {
        // Return strategy name
        return this.strategyName;
    // End of method
    }

    // Helper method indicating if any votes have been cast
    public boolean hasVotes() {
        // Return true if total votes is strictly greater than zero
        return this.totalVotes > 0;
    // End of method
    }

    // Generates a human-readable text summary of the winning status
    public String getWinnerDescription() {
        // Check if custom verdict is supplied by Strategy
        if (customVerdict != null && !customVerdict.isEmpty()) {
            // Return custom strategy verdict
            return this.customVerdict;
        // End if
        }
        // Check if no votes have been cast yet
        if (totalVotes == 0) {
            // Return status indicating no votes
            return "No votes recorded yet.";
        // End if
        }
        // Check if there is a tie between multiple options
        if (tie) {
            // Return formatted tie notification
            return "Tie between: " + String.join(", ", winningOptions);
        // End if
        }
        // Check if a single leader exists
        if (!winningOptions.isEmpty()) {
            // Return formatted winner announcement
            return "Leading Option: " + winningOptions.get(0);
        // End if
        }
        // Fallback description
        return "Indeterminate result.";
    // End of method
    }
// End of class
}
