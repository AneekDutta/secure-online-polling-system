// Package declaration for algorithmic strategy pattern implementations
package com.polling.strategy;

// Import ArrayList
import java.util.ArrayList;
// Import List
import java.util.List;
// Import Map
import java.util.Map;

// Majority threshold strategy requiring greater than 50% of total votes to declare an outright winner
public class MajorityWinningStrategy implements WinningStrategy {

    // Returns strategy title
    @Override
    public String getStrategyName() {
        // Return title
        return "Majority Threshold (>50% Required)";
    // End of method
    }

    // Returns rule explanation
    @Override
    public String getRuleExplanation() {
        // Return explanation
        return "An option must receive strictly more than 50% of all votes to win outright. Otherwise, a Runoff is mandated.";
    // End of method
    }

    // Evaluates winners based on strict majority threshold
    @Override
    public StrategyEvaluationResult evaluateWinners(Map<String, Integer> counts, int totalVotes) {
        // Check for empty or zero votes
        if (counts == null || counts.isEmpty() || totalVotes == 0) {
            // Return zero-vote outcome
            return new StrategyEvaluationResult(new ArrayList<>(), false, false, "No votes cast yet.");
        // End if
        }

        // Variable tracking highest vote tally
        int highestCount = -1;
        // String tracking top option
        String topOption = null;
        // Count threshold needed: strictly greater than half
        double majorityNeeded = totalVotes / 2.0;

        // Iterate through entries to find leading candidate
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            // Check if count strictly exceeds current highest
            if (entry.getValue() > highestCount) {
                // Update highest count
                highestCount = entry.getValue();
                // Set top option
                topOption = entry.getKey();
            // End if
            }
        // End for
        }

        // Check if top candidate achieves > 50% majority
        if (highestCount > majorityNeeded && topOption != null) {
            // List containing sole majority winner
            List<String> winners = new ArrayList<>();
            // Add top option
            winners.add(topOption);
            // Calculate percentage
            double pct = Math.round(((double) highestCount / (double) totalVotes) * 1000.0) / 10.0;
            // Build majority description
            String description = "Absolute Majority Winner: " + topOption + " (" + highestCount + " / " + totalVotes + " votes, " + pct + "% > 50%)";
            // Return majority winner result
            return new StrategyEvaluationResult(winners, false, false, description);
        // If no candidate achieved > 50%
        } else {
            // Collect top two candidates for runoff recommendation
            List<String> topCandidates = new ArrayList<>();
            // Re-scan to find contenders with the highest vote tallies
            for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                // If candidate count equals highest
                if (entry.getValue() == highestCount && highestCount > 0) {
                    // Add candidate
                    topCandidates.add(entry.getKey());
                // End if
                }
            // End for
            }

            // Description for runoff
            String description = "No candidate achieved >50% majority. Runoff election recommended among leaders: " + String.join(" and ", topCandidates);
            // Return runoff result
            return new StrategyEvaluationResult(topCandidates, topCandidates.size() > 1, true, description);
        // End if
        }
    // End of method
    }
// End of class
}
