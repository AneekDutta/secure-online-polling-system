// Package declaration for domain model entities
package com.polling.model;

// Import SecurityUtil for cryptographic SHA-256 voter hashing
import com.polling.security.SecurityUtil;
// Import ArrayList for ordered option sequence
import java.util.ArrayList;
// Import Collections utility class for defensive unmodifiable wrappers
import java.util.Collections;
// Import HashMap for storing vote tallies per option (demonstrating HashMap usage)
import java.util.HashMap;
// Import HashSet for tracking unique cryptographic voter hashes (demonstrating HashSet usage)
import java.util.HashSet;
// Import List interface
import java.util.List;
// Import Map interface
import java.util.Map;
// Import Set interface
import java.util.Set;
// Import Date for timestamping creation time
import java.util.Date;

// Represents an individual poll containing questions, options, live tallies, and hashed voter audit sets
public class Poll {

    // Private field storing the unique identifier of the poll (Encapsulation)
    private final String pollId;

    // Private field storing the poll question prompt
    private final String question;

    // Private list storing the available candidate options
    private final List<String> options;

    // Private HashMap mapping each option text to its accumulated vote count
    private final Map<String, Integer> voteCounts;

    // Private HashSet maintaining the unique SHA-256 hashes of voters to enforce single-vote integrity
    private final Set<String> votedVoterHashes;

    // Private HashMap storing issued ballot receipts indexed by receipt verification code
    private final Map<String, VoteReceipt> issuedReceipts;

    // Private boolean flag indicating whether the poll is actively accepting votes
    private boolean open;

    // Private Date recording the creation timestamp of the poll
    private final Date createdAt;

    // Parameterized constructor initializing a new Poll instance
    public Poll(String pollId, String question, List<String> options) {
        // Assign the unique poll ID
        this.pollId = (pollId != null) ? pollId.trim() : "";
        // Assign the poll question
        this.question = (question != null) ? question.trim() : "";
        // Initialize the internal option list
        this.options = new ArrayList<>();
        // Initialize the HashMap for vote counts
        this.voteCounts = new HashMap<>();
        // Initialize the HashSet for cryptographic voter hash tracking
        this.votedVoterHashes = new HashSet<>();
        // Initialize the HashMap for issued receipts
        this.issuedReceipts = new HashMap<>();
        // Set the initial status to open
        this.open = true;
        // Record the current system date and time
        this.createdAt = new Date();
        // Check if options list provided is non-null
        if (options != null) {
            // Iterate through every candidate option provided
            for (String option : options) {
                // Verify that the option string is non-null and not blank
                if (option != null && !option.trim().isEmpty()) {
                    // Normalize option text by trimming
                    String cleanOption = option.trim();
                    // Avoid duplicate option entries in the poll definition
                    if (!this.options.contains(cleanOption)) {
                        // Append option to the option sequence list
                        this.options.add(cleanOption);
                        // Initialize vote counter in HashMap to 0
                        this.voteCounts.put(cleanOption, 0);
                    // End of duplicate check
                    }
                // End of validation check
                }
            // End of options iteration
            }
        // End of null check
        }
    // End of constructor
    }

    // Public getter for retrieving the poll ID
    public String getPollId() {
        // Return encapsulated poll ID
        return this.pollId;
    // End of method
    }

    // Public getter for retrieving the question
    public String getQuestion() {
        // Return encapsulated question
        return this.question;
    // End of method
    }

    // Public getter returning an unmodifiable view of options (defensive programming)
    public List<String> getOptions() {
        // Return unmodifiable view to preserve encapsulation
        return Collections.unmodifiableList(this.options);
    // End of method
    }

    // Public getter returning a defensive copy of the vote count HashMap
    public Map<String, Integer> getVoteCounts() {
        // Synchronize on voteCounts map during copy to ensure thread-safety
        synchronized (this.voteCounts) {
            // Return a new HashMap clone to protect internal state
            return new HashMap<>(this.voteCounts);
        // End of synchronized block
        }
    // End of method
    }

    // Retrieves the current vote count for a specific option
    public int getVoteCountForOption(String option) {
        // Synchronize access to voteCounts map
        synchronized (this.voteCounts) {
            // Check if option exists in map
            if (option != null && this.voteCounts.containsKey(option)) {
                // Return accumulated vote count
                return this.voteCounts.get(option);
            // End if
            }
            // Return 0 if option is invalid or unlisted
            return 0;
        // End of synchronized block
        }
    // End of method
    }

    // Calculates the total sum of all votes cast across all options
    public int getTotalVotes() {
        // Synchronize access to voteCounts map
        synchronized (this.voteCounts) {
            // Initialize vote counter accumulator
            int sum = 0;
            // Iterate over all integer vote values in the map
            for (int count : this.voteCounts.values()) {
                // Add current count to accumulator
                sum += count;
            // End of loop
            }
            // Return computed total votes
            return sum;
        // End of synchronized block
        }
    // End of method
    }

    // Checks whether a given voter ID has already cast a ballot using SHA-256 hashing
    public boolean hasVoted(String voterId) {
        // Synchronize access to the voter tracking HashSet
        synchronized (this.votedVoterHashes) {
            // Check if voter ID is non-null
            if (voterId == null || voterId.trim().isEmpty()) {
                // Null or empty voter ID cannot be confirmed as voted
                return false;
            // End if
            }
            // Compute salted SHA-256 hash of voter ID
            String voterHash = SecurityUtil.hashVoterId(voterId, this.pollId);
            // Perform fast O(1) containment lookup using HashSet
            return this.votedVoterHashes.contains(voterHash);
        // End of synchronized block
        }
    // End of method
    }

    // Records a vote and generates a verifiable VoteReceipt
    public synchronized VoteReceipt recordVoteWithReceipt(String voterId, String option) {
        // Clean option text
        String cleanOption = (option != null) ? option.trim() : "";
        // Check if poll is closed
        if (!this.open) {
            // Reject vote recording if poll is inactive
            return null;
        // End if
        }
        // Verify voter ID presence
        if (voterId == null || voterId.trim().isEmpty()) {
            // Reject vote recording for missing voter ID
            return null;
        // End if
        }
        // Verify option exists in this poll
        if (!this.options.contains(cleanOption)) {
            // Reject vote recording for unlisted option
            return null;
        // End if
        }
        // Compute cryptographic SHA-256 hash for voter ID with poll salt
        String voterHash = SecurityUtil.hashVoterId(voterId, this.pollId);
        // Check duplicate status in HashSet
        if (this.votedVoterHashes.contains(voterHash)) {
            // Duplicate detected, reject vote
            return null;
        // End if
        }
        // Add voter hash to the HashSet
        this.votedVoterHashes.add(voterHash);
        // Retrieve existing count for selected option
        int currentCount = this.voteCounts.getOrDefault(cleanOption, 0);
        // Increment vote count and store back into HashMap
        this.voteCounts.put(cleanOption, currentCount + 1);
        // Generate cryptographic receipt code
        String receiptCode = SecurityUtil.generateReceiptCode(this.pollId, voterHash, cleanOption);
        // Construct VoteReceipt object
        VoteReceipt receipt = new VoteReceipt(receiptCode, this.pollId, voterHash, cleanOption);
        // Store receipt in HashMap
        this.issuedReceipts.put(receiptCode, receipt);
        // Return generated receipt
        return receipt;
    // End of method
    }

    // Standard recordVote delegate returning boolean for backwards compatibility
    public boolean recordVote(String voterId, String option) {
        // Delegate to recordVoteWithReceipt and check non-null
        return recordVoteWithReceipt(voterId, option) != null;
    // End of method
    }

    // Verifies whether a given receipt code corresponds to a valid ballot in this poll
    public VoteReceipt verifyReceipt(String receiptCode) {
        // Synchronize access to receipts map
        synchronized (this.issuedReceipts) {
            // Return receipt if present
            return (receiptCode != null) ? this.issuedReceipts.get(receiptCode.trim().toUpperCase()) : null;
        // End of synchronized block
        }
    // End of method
    }

    // Checks if the poll is currently accepting votes
    public boolean isOpen() {
        // Return open state boolean flag
        return this.open;
    // End of method
    }

    // Closes the poll so that no further votes can be accepted
    public synchronized void closePoll() {
        // Set open state to false
        this.open = false;
    // End of method
    }

    // Reopens the poll if administrative override is requested
    public synchronized void openPoll() {
        // Set open state to true
        this.open = true;
    // End of method
    }

    // Returns the total number of unique voters who participated
    public int getVoterCount() {
        // Synchronize access to voter hash HashSet
        synchronized (this.votedVoterHashes) {
            // Return number of elements stored in the HashSet
            return this.votedVoterHashes.size();
        // End of synchronized block
        }
    // End of method
    }

    // Returns an unmodifiable view of all SHA-256 voter hashes (for auditing)
    public Set<String> getVotedVoterHashes() {
        // Synchronize access to voter hash HashSet
        synchronized (this.votedVoterHashes) {
            // Return defensive unmodifiable copy of the HashSet
            return Collections.unmodifiableSet(new HashSet<>(this.votedVoterHashes));
        // End of synchronized block
        }
    // End of method
    }

    // Backwards-compatible alias returning the voter hashes set
    public Set<String> getVotedVoterIds() {
        // Delegate to getVotedVoterHashes
        return getVotedVoterHashes();
    // End of method
    }

    // Returns an unmodifiable map of issued vote receipts
    public Map<String, VoteReceipt> getIssuedReceipts() {
        // Synchronize access to receipts map
        synchronized (this.issuedReceipts) {
            // Return defensive copy
            return Collections.unmodifiableMap(new HashMap<>(this.issuedReceipts));
        // End of synchronized block
        }
    // End of method
    }

    // Returns the timestamp when the poll was created
    public Date getCreatedAt() {
        // Return defensive copy of the creation date
        return new Date(this.createdAt.getTime());
    // End of method
    }

    // Provides a readable summary of poll details
    @Override
    public String toString() {
        // Construct descriptive poll summary string
        return "Poll #" + pollId + ": \"" + question + "\" [Status=" + (open ? "OPEN" : "CLOSED") + ", Votes=" + getTotalVotes() + "]";
    // End of method
    }
// End of class
}
