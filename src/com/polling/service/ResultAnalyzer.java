//ROLE: Tallying, Percentage Calculation & Strategy-Based Outcome Evaluation
//WHAT IT DOES:-
//1. Tally & Percentages: Reads raw vote counts from a Poll, preserves option ordering using LinkedHashMap, 
//                        and computes exact percentage shares rounded to 1 decimal place (avoiding division-by-zero if 0 votes are cast).
//2. Strategy Pattern: Instead of hardcoding winner logic, it delegates the outcome evaluation to a WinningStrategy (defaults to 
//                     PluralityWinningStrategy, but supports Majority/Runoff strategies).
//3. Produces Summary: Packages all computed metrics into an immutable PollResultSummary object.


// Package declaration for service layer components
package com.polling.service;

// Import Poll entity model
import com.polling.model.Poll;
// Import Plurality strategy as default
import com.polling.strategy.PluralityWinningStrategy;
// Import StrategyEvaluationResult
import com.polling.strategy.StrategyEvaluationResult;
// Import WinningStrategy interface
import com.polling.strategy.WinningStrategy;
// Import ArrayList for mutable list manipulation
import java.util.ArrayList;
// Import LinkedHashMap to maintain deterministic option ordering during analysis
import java.util.LinkedHashMap;
// Import List interface
import java.util.List;
// Import Map interface
import java.util.Map;

// Component dedicated to computing analytics, percentages, and winners using Strategy Pattern
public class ResultAnalyzer {

    // Default plurality winning strategy instance
    private static final WinningStrategy DEFAULT_STRATEGY = new PluralityWinningStrategy();

    // Analyzes a poll using the default Plurality electoral strategy
    public PollResultSummary analyze(Poll poll) {
        // Delegate to strategy-aware overload
        return analyze(poll, DEFAULT_STRATEGY);
    // End of method
    }

    // Analyzes a given poll with a specified WinningStrategy (demonstrating Strategy Pattern)
    public PollResultSummary analyze(Poll poll, WinningStrategy strategy) {
        // Guard against null poll argument
        if (poll == null) {
            // Return empty result summary if poll is null
            return new PollResultSummary("", "None", false, 0, new LinkedHashMap<>(), new LinkedHashMap<>(), new ArrayList<>(), false);
        // End if
        }
        // Fallback to default strategy if null supplied
        WinningStrategy activeStrategy = (strategy != null) ? strategy : DEFAULT_STRATEGY;
        // Extract total votes cast across all options
        int totalVotes = poll.getTotalVotes();
        // Fetch raw vote tallies from the poll
        Map<String, Integer> rawCounts = poll.getVoteCounts();
        // Ordered map to store counts preserving option order
        Map<String, Integer> orderedCounts = new LinkedHashMap<>();
        // Ordered map to store computed percentages
        Map<String, Double> percentages = new LinkedHashMap<>();

        // Iterate through all poll options in their original order
        for (String option : poll.getOptions()) {
            // Retrieve vote count for this option, defaulting to 0
            int count = rawCounts.getOrDefault(option, 0);
            // Put count into ordered map
            orderedCounts.put(option, count);
            // Calculate percentage share
            double pct = 0.0;
            // Check if any votes were cast overall to avoid division by zero
            if (totalVotes > 0) {
                // Compute percentage: (count / total) * 100
                pct = ((double) count / (double) totalVotes) * 100.0;
                // Round to one decimal place for presentation accuracy
                pct = Math.round(pct * 10.0) / 10.0;
            // End if
            }
            // Put computed percentage into ordered map
            percentages.put(option, pct);
        // End for
        }

        // Delegate electoral winner evaluation to the injected WinningStrategy
        StrategyEvaluationResult stratResult = activeStrategy.evaluateWinners(orderedCounts, totalVotes);

        // Construct and return immutable result summary object
        return new PollResultSummary(
            // Poll ID
            poll.getPollId(),
            // Poll question
            poll.getQuestion(),
            // Open status
            poll.isOpen(),
            // Total vote tally
            totalVotes,
            // Option counts map
            orderedCounts,
            // Option percentages map
            percentages,
            // Leading options list determined by strategy
            stratResult.getWinningOptions(),
            // Tie indicator flag
            stratResult.isTie(),
            // Runoff requirement flag
            stratResult.isRequiresRunoff(),
            // Strategy name
            activeStrategy.getStrategyName(),
            // Strategy verdict description
            stratResult.getDescription()
        );
    // End of method
    }
// End of class
}
