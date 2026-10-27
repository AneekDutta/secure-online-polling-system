//ROLE: Guard against invalid, missing, or mistyped poll identifiers.
//THROWN WHEN: 1. In PollManager.getPoll
 //               if the pollId argument is null/empty 
 //               or if pollCatalog.get(cleanId)[LINE-176] returns null.


//Prevents NullPointerException



// Package declaration for custom exceptions
package com.polling.exception;

// Exception thrown when a requested poll ID cannot be found in storage
public class PollNotFoundException extends PollingException {

    // Unique serial version identifier for Serializable class
    private static final long serialVersionUID = 1L;

    // Constructor accepting an explanatory error message
    public PollNotFoundException(String message) {
        // Pass the error message to the parent PollingException constructor
        super(message);
    // End of constructor
    }
// End of class
}
