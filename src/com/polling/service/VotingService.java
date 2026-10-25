// Package declaration for service layer components
package com.polling.service;

// Import PollEvent for Observer pattern
import com.polling.event.PollEvent;
// Import PollEventType enum
import com.polling.event.PollEventType;
// Import duplicate vote exception
import com.polling.exception.DuplicateVoteException;
// Import invalid vote exception
import com.polling.exception.InvalidVoteException;
// Import poll closed exception
import com.polling.exception.PollClosedException;
// Import poll not found exception
import com.polling.exception.PollNotFoundException;
// Import general validation exception
import com.polling.exception.ValidationException;
// Import Poll domain model
import com.polling.model.Poll;
// Import VoteReceipt domain model
import com.polling.model.VoteReceipt;
// Import Voter domain model
import com.polling.model.Voter;

// Core service handling business logic, validation rules, secure ballot submission, and observer dispatch
public class VotingService {

    // Reference to the PollManager providing access to the poll catalog
    private final PollManager pollManager;

    // Parameterized constructor injecting the PollManager dependency
    public VotingService(PollManager pollManager) {
        // Assign injected poll manager instance
        this.pollManager = pollManager;
    // End of constructor
    }

    // Submits a vote and returns an issued cryptographic VoteReceipt while notifying observers
    public synchronized VoteReceipt castVoteWithReceipt(String pollId, String rawVoterId, String selectedOption)
            throws PollNotFoundException, PollClosedException, DuplicateVoteException, InvalidVoteException, ValidationException {

        // Validate voter ID parameter presence
        if (rawVoterId == null || rawVoterId.trim().isEmpty()) {
            // Throw exception if voter ID is blank
            throw new ValidationException("Voter ID cannot be empty or whitespace.");
        // End if
        }
        // Normalize voter ID string
        String voterId = rawVoterId.trim().toUpperCase();
        // Check minimum length for reasonable voter identification
        if (voterId.length() < 3) {
            // Reject excessively short voter IDs
            throw new ValidationException("Voter ID must be at least 3 characters long.");
        // End if
        }
        // Validate poll ID parameter presence
        if (pollId == null || pollId.trim().isEmpty()) {
            // Throw exception if poll ID is blank
            throw new ValidationException("A target Poll ID must be specified.");
        // End if
        }
        // Validate selected option presence
        if (selectedOption == null || selectedOption.trim().isEmpty()) {
            // Throw exception if no option was chosen
            throw new InvalidVoteException("Please select an option before casting your vote.");
        // End if
        }
        // Clean selected option string
        String cleanOption = selectedOption.trim();
        // Retrieve target poll from manager (throws PollNotFoundException if not found)
        Poll poll = this.pollManager.getPoll(pollId);
        // Verify that the poll is currently open
        if (!poll.isOpen()) {
            // Throw PollClosedException if voting is closed on this poll
            throw new PollClosedException("Poll #" + pollId + " is CLOSED. Subsequent votes are strictly rejected.");
        // End if
        }
        // Check for duplicate vote using the poll's cryptographic SHA-256 hash set
        if (poll.hasVoted(voterId)) {
            // Throw DuplicateVoteException if voter ID hash already exists in HashSet
            throw new DuplicateVoteException("Voter ID '" + voterId + "' has already cast a ballot in Poll #" + pollId + ".");
        // End if
        }
        // Verify that selected option is listed in the poll's authorized options
        if (!poll.getOptions().contains(cleanOption)) {
            // Throw InvalidVoteException if option is not recognized
            throw new InvalidVoteException("Option '" + cleanOption + "' is not a valid choice for this poll.");
        // End if
        }
        // Atomically record vote in the poll model and issue cryptographic receipt
        VoteReceipt receipt = poll.recordVoteWithReceipt(voterId, cleanOption);
        // If recording failed inside model synchronization
        if (receipt == null) {
            // Throw duplicate vote exception as secondary defense
            throw new DuplicateVoteException("Vote could not be recorded due to a duplicate submission collision.");
        // End if
        }
        // Dispatch observer event so all open UI panels update in real time
        this.pollManager.notifyEvent(new PollEvent(PollEventType.VOTE_CAST, pollId, "New vote recorded for \"" + cleanOption + "\" in Poll #" + pollId));
        // Return issued ballot receipt
        return receipt;
    // End of method
    }

    // Backwards-compatible castVote method returning boolean
    public synchronized boolean castVote(String pollId, String rawVoterId, String selectedOption)
            throws PollNotFoundException, PollClosedException, DuplicateVoteException, InvalidVoteException, ValidationException {
        // Delegate to castVoteWithReceipt and verify non-null
        return castVoteWithReceipt(pollId, rawVoterId, selectedOption) != null;
    // End of method
    }

    // Verifies whether a receipt code is valid for a given poll
    public VoteReceipt verifyReceipt(String pollId, String receiptCode) throws PollNotFoundException {
        // Retrieve target poll
        Poll poll = this.pollManager.getPoll(pollId);
        // Delegate verification to poll model
        return poll.verifyReceipt(receiptCode);
    // End of method
    }

    // Helper method to check if a specific voter ID can vote in a given poll
    public boolean canVote(String pollId, String voterId) {
        // Return false for invalid input
        if (pollId == null || voterId == null || voterId.trim().isEmpty()) {
            // Guard return
            return false;
        // End if
        }
        // Try to retrieve poll safely
        try {
            // Fetch target poll
            Poll poll = this.pollManager.getPoll(pollId);
            // Can vote if poll is open AND voter has not already voted
            return poll.isOpen() && !poll.hasVoted(voterId.trim().toUpperCase());
        // Catch exception if poll is missing
        } catch (PollNotFoundException e) {
            // Poll not found, cannot vote
            return false;
        // End try-catch
        }
    // End of method
    }
// End of class
}
