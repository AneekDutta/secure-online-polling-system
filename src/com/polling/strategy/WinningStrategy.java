//ROLE: Strategy Interface defining the standard contract for electoral counting rules
//WHAT IT DOES:-
//1. Establishes the Blueprint: Declares getStrategyName(), getRuleExplanation(), and evaluateWinners().
//2. Decouples Calculation from Services: Allows ResultAnalyzer to evaluate results without hardcoding specific mathematical formulas.
//3. Supports Open/Closed Principle: New election methods (e.g., Ranked-Choice) can be added without altering existing code.


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
