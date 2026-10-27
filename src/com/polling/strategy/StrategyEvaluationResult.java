// Package declaration for algorithmic strategy pattern implementations
package com.polling.strategy;

// Import Collections utility class
import java.util.Collections;
// Import List interface
import java.util.List;

// Encapsulates the algorithmic verdict produced by a WinningStrategy
public class StrategyEvaluationResult {

    // List of option titles judged as winners under this strategy
    private final List<String> winningOptions;

    // Flag indicating whether multiple candidates tied
    private final boolean tie;

    // Flag indicating whether a runoff election is mandated
    private final boolean requiresRunoff;

    // Descriptive explanation of how the strategy arrived at the outcome
    private final String description;

    // Parameterized constructor initializing the strategy outcome
    public StrategyEvaluationResult(List<String> winningOptions, boolean tie, boolean requiresRunoff, String description) {
        // Assign winners defensively
        this.winningOptions = (winningOptions != null) ? Collections.unmodifiableList(winningOptions) : Collections.emptyList();
        // Assign tie flag
        this.tie = tie;
        // Assign runoff flag
        this.requiresRunoff = requiresRunoff;
        // Assign description text
        this.description = (description != null) ? description.trim() : "";
    // End of constructor
    }

    // Public getter for winning options list
    public List<String> getWinningOptions() {
        // Return winners
        return this.winningOptions;
    // End of method
    }

    // Public getter for tie indicator
    public boolean isTie() {
        // Return tie flag
        return this.tie;
    // End of method
    }

    // Public getter for runoff indicator
    public boolean isRequiresRunoff() {
        // Return runoff flag
        return this.requiresRunoff;
    // End of method
    }

    // Public getter for descriptive verdict
    public String getDescription() {
        // Return description
        return this.description;
    // End of method
    }
// End of class
}
