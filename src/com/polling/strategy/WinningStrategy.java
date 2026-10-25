// Package declaration for algorithmic strategy pattern implementations
package com.polling.strategy;

// Import Map interface
import java.util.Map;

// Strategy interface defining the contract for determining electoral winners from vote tallies
public interface WinningStrategy {

    // Returns a short, human-readable name for the strategy (e.g., "Plurality (First-Past-The-Post)")
    String getStrategyName();

    // Returns a detailed description of the electoral calculation rule
    String getRuleExplanation();

    // Evaluates vote tallies to determine winners, ties, or runoff mandates
    StrategyEvaluationResult evaluateWinners(Map<String, Integer> counts, int totalVotes);
// End of interface
}
