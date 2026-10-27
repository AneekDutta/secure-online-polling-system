//Role: Protects ballot submission integrity against malformed or illegal vote payloads.
//IT IS THROWN WHEN: 1. In VotingService.castVoteWithReceipt
//                      if no option was selected
//                   2. In VotingService.castVoteWithReceipt
//                      if the submitted candidate exception does not exist in poll.getOptions()





// Package declaration for custom exceptions
package com.polling.exception;

// Exception thrown when vote parameters are malformed or invalid
public class InvalidVoteException extends PollingException {

    // Unique serial version identifier for Serializable class
    private static final long serialVersionUID = 1L;

    // Constructor accepting an explanatory error message
    public InvalidVoteException(String message) {
        // Pass the error message to the parent PollingException constructor
        super(message);
    // End of constructor
    }
// End of class
}
