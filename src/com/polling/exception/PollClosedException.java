//ROLE: Enforces poll lifecycle boundaries.
//THROWN WHEN: 1. In VotingService.castVoteWithReceipt
//                when !poll.isOpen()
//                LINE-72





// Package declaration for custom exceptions
package com.polling.exception;

// Exception thrown when an action like voting is attempted on a closed poll
public class PollClosedException extends PollingException {

    // Unique serial version identifier for Serializable class
    private static final long serialVersionUID = 1L;

    // Constructor accepting an explanatory error message
    public PollClosedException(String message) {
        // Pass the error message to the parent PollingException constructor
        super(message);
    // End of constructor
    }
// End of class
}
