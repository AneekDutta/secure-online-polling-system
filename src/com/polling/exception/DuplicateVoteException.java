//Enforces the fundamental democratic principle: one person, one vote.
//IT IS THROWN WHEN: 1. In VotingService.castVoteWithReceipt 
//                      when poll.hasVoted(voterId) returns true 
//                      (checking against the poll's HashSet of SHA-256 hashed voter IDs).
//                      LINE-38
//                   2. As a secondary collision defense if atomic ballot recording fails
//                       LINE-91-96

// Package declaration for custom exceptions
package com.polling.exception;

// Exception thrown when a voter attempts to vote more than once in the same poll
public class DuplicateVoteException extends PollingException {

    // Unique serial version identifier for Serializable class
    private static final long serialVersionUID = 1L;

    // Constructor accepting an explanatory error message
    public DuplicateVoteException(String message) {
        // Pass the error message to the parent PollingException constructor
        super(message);
    // End of constructor
    }
// End of class
}
