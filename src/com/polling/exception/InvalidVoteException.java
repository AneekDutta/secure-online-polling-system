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
