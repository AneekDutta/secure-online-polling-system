// Package declaration for service layer components
package com.polling.service;

// Import Poll entity model
import com.polling.model.Poll;
// Import ArrayList for mutable list manipulation
import java.util.ArrayList;
// Import LinkedHashMap to maintain deterministic option ordering during analysis
import java.util.LinkedHashMap;
// Import List interface
import java.util.List;
// Import Map interface
import java.util.Map;

// Component dedicated to computing analytics, percentages, and winners for polls
public class ResultAnalyzer {

    // Analyzes a given poll and compiles a comprehensive result summary
    public PollResultSummary analyze(Poll poll) {
        // Guard against null poll argument
        if (poll == null) {
            // Return empty result summary if poll is null
            return new PollResultSummary("", "None", false, 0, new LinkedHashMap<>(), new LinkedHashMap<>(), new ArrayList<>(), false);
        // End if
        }
        // Extract total votes cast across all options
        int totalVotes = poll.getTotalVotes();
        // Fetch raw vote tallies from the poll
        Map<String, Integer> rawCounts = poll.getVoteCounts();
        // Ordered map to store counts preserving option order
        Map<String, Integer> orderedCounts = new LinkedHashMap<>();
        // Ordered map to store computed percentages
        Map<String, Double> percentages = new LinkedHashMap<>();
        // Tracker for the highest vote count observed
        int highestCount = -1;
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
            // Update highest observed vote count
            if (count > highestCount) {
                // Set new highest count
                highestCount = count;
            // End if
            }
        // End for
        }
        // List to hold options with the highest vote count
        List<String> leaders = new ArrayList<>();
        // Determine whether there are any votes and who holds the top count
        if (totalVotes > 0 && highestCount > 0) {
            // Scan through all options to identify all top vote getters
            for (Map.Entry<String, Integer> entry : orderedCounts.entrySet()) {
                // Check if current option matches the highest count
                if (entry.getValue() == highestCount) {
                    // Add this option to leader list
                    leaders.add(entry.getKey());
                // End if
                }
            // End for
            }
        // End if
        }
        // A tie occurs if more than one option shares the top vote count
        boolean isTie = (leaders.size() > 1);
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
            // Leading options list
            leaders,
            // Tie indicator flag
            isTie
        );
    // End of method
    }
// End of class
}
