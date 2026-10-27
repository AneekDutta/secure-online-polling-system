//ROLE: First-Past-The-Post (Plurality) Electoral Strategy
//WHAT IT DOES:-
//1. Finds Highest Vote Count: Scans all options and identifies the maximum tally.
//2. Declares Direct Winner: Whichever candidate has the highest vote wins, regardless of whether they reached 50%.
//3. Detects Equal Ties: Flags isTie = true if two or more options share the top score.
//4. Concludes Election: Never triggers a runoff election(second round for voting) (requiresRunoff = false).


// Package declaration for algorithmic strategy pattern implementations
package com.polling.strategy;

// Import ArrayList
import java.util.ArrayList;
// Import List
import java.util.List;
// Import Map
import java.util.Map;

// Standard First-Past-The-Post strategy where candidate(s) with highest votes win
public class PluralityWinningStrategy implements WinningStrategy {

    // Returns strategy title
    @Override
    public String getStrategyName() {
        // Return title
        return "Plurality (First-Past-The-Post)";
    // End of method
    }

    // Returns rule explanation
    @Override
    public String getRuleExplanation() {
        // Return explanation
        return "The option with the highest total vote count wins directly. In case of equal top counts, a tie is declared.";
    // End of method
    }

    // Evaluates winners based on plurality rule
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
        // Iterate through counts map to find maximum count
        for (int count : counts.values()) {
            // Check if count exceeds highest
            if (count > highestCount) {
                // Update highest count
                highestCount = count;
            // End if
            }
        // End for
        }

        // List collecting all options achieving the highest tally
        List<String> winners = new ArrayList<>();
        // Scan entries to match top tally
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            // Check if count equals maximum
            if (entry.getValue() == highestCount && highestCount > 0) {
                // Add option to winners
                winners.add(entry.getKey());
            // End if
            }
        // End for
        }

        // Tie flag if more than one candidate shares highest count
        boolean isTie = (winners.size() > 1);
        // Build description string
        String description;
        // Determine description based on winners
        if (winners.isEmpty()) {
            // No votes
            description = "No votes recorded.";
        // If tie
        } else if (isTie) {
            // Tie description
            description = "Plurality Tie between: " + String.join(", ", winners) + " (" + highestCount + " votes each)";
        // Single winner
        } else {
            // Single winner description
            description = "Leading Option: " + winners.get(0) + " (" + highestCount + " votes)";
        // End if
        }

        // Return strategy evaluation result
        return new StrategyEvaluationResult(winners, isTie, false, description);
    // End of method
    }
// End of class
}
