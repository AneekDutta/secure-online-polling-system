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
