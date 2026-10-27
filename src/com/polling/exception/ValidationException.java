//ROLE: General validation guard for entity creation, 
//        format constraints, and access permissions.

//WHEN IT IS THROWN: 1. IN PollManager.createPoll :
//                         -If the poll question is blank.(LINE 111)
//                         -If fewer than two non-empty, distinct options are provided.

//                   2. In PollManager.closePoll: (LINE-196)
 //                       if a non-admin user attempts to close a poll.

//                   3. In VotingService.castVoteWithReceipt:(LINE 38)
//                        -If voterId is missing, whitespace, or shorter than 3 characters.
//                        -If pollId parameter is blank


// Package declaration for custom exceptions
package com.polling.exception;

// Exception thrown when data input fails validation checks
public class ValidationException extends PollingException {

    // Unique serial version identifier for Serializable class
    private static final long serialVersionUID = 1L;

    // Constructor accepting an explanatory error message
    public ValidationException(String message) {
        // Pass the error message to the parent PollingException constructor
        super(message);
    // End of constructor
    }
// End of class
}
