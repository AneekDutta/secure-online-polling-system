// Package declaration for custom exceptions
package com.polling.exception;

// Base exception class for all polling system domain errors
public class PollingException extends Exception {

    // Unique serial version identifier for Serializable class
    private static final long serialVersionUID = 1L;

    // Constructor accepting an explanatory error message
    public PollingException(String message) {
        // Delegate message to superclass Exception
        super(message);
    // End of constructor
    }

    // Constructor accepting an explanatory error message and root cause
    public PollingException(String message, Throwable cause) {
        // Delegate message and cause to superclass Exception
        super(message, cause);
    // End of constructor
    }
// End of class
}
