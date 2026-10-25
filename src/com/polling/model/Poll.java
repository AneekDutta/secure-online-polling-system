// Package declaration for domain model entities
package com.polling.model;

// Import ArrayList for ordered option sequence
import java.util.ArrayList;
// Import Collections utility class for defensive unmodifiable wrappers
import java.util.Collections;
// Import HashMap for storing vote tallies per option (demonstrating HashMap usage)
import java.util.HashMap;
// Import HashSet for tracking unique voter IDs (demonstrating HashSet usage)
import java.util.HashSet;
// Import List interface
import java.util.List;
// Import Map interface
import java.util.Map;
// Import Set interface
import java.util.Set;
// Import Date for timestamping creation time
import java.util.Date;

// Represents an individual poll containing questions, options, and live tallies
public class Poll {

    // Private field storing the unique identifier of the poll (Encapsulation)
    private final String pollId;

    // Private field storing the poll question prompt
    private final String question;

    // Private list storing the available candidate options
    private final List<String> options;

    // Private HashMap mapping each option text to its accumulated vote count
    private final Map<String, Integer> voteCounts;

    // Private HashSet maintaining the unique IDs of voters who already voted
    private final Set<String> votedVoterIds;

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
        // Initialize the HashSet for voter tracking
        this.votedVoterIds = new HashSet<>();
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

    // Checks whether a given voter ID has already cast a ballot in this poll
    public boolean hasVoted(String voterId) {
        // Synchronize access to the voter tracking HashSet
        synchronized (this.votedVoterIds) {
            // Check if voter ID is non-null and present in HashSet
            if (voterId == null) {
                // Null voter ID cannot be confirmed as voted
                return false;
            // End if
            }
            // Perform fast O(1) containment lookup using HashSet
            return this.votedVoterIds.contains(voterId.trim().toUpperCase());
        // End of synchronized block
        }
    // End of method
    }

    // Records a vote for the specified option and logs the voter ID in the HashSet
    public boolean recordVote(String voterId, String option) {
        // Clean voter ID and format to uppercase for case-insensitive uniqueness
        String cleanVoterId = (voterId != null) ? voterId.trim().toUpperCase() : "";
        // Clean option text
        String cleanOption = (option != null) ? option.trim() : "";
        // Check if poll is closed
        if (!this.open) {
            // Reject vote recording if poll is inactive
            return false;
        // End if
        }
        // Verify voter ID is non-empty
        if (cleanVoterId.isEmpty()) {
            // Reject vote recording for missing voter ID
            return false;
        // End if
        }
        // Verify option exists in this poll
        if (!this.options.contains(cleanOption)) {
            // Reject vote recording for unlisted option
            return false;
        // End if
        }
        // Synchronize on the poll instance to ensure atomic ballot processing
        synchronized (this) {
            // Re-check duplicate status inside synchronized critical section
            if (this.votedVoterIds.contains(cleanVoterId)) {
                // Duplicate vote detected, operation rejected
                return false;
            // End if
            }
            // Add voter ID to the HashSet (returns true if successfully added)
            this.votedVoterIds.add(cleanVoterId);
            // Retrieve existing count for selected option
            int currentCount = this.voteCounts.getOrDefault(cleanOption, 0);
            // Increment vote count and store back into HashMap
            this.voteCounts.put(cleanOption, currentCount + 1);
            // Vote successfully registered
            return true;
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
        // Synchronize access to voter ID HashSet
        synchronized (this.votedVoterIds) {
            // Return number of elements stored in the HashSet
            return this.votedVoterIds.size();
        // End of synchronized block
        }
    // End of method
    }

    // Returns an unmodifiable view of all voter IDs who voted (for auditing)
    public Set<String> getVotedVoterIds() {
        // Synchronize access to voter ID HashSet
        synchronized (this.votedVoterIds) {
            // Return defensive unmodifiable copy of the HashSet
            return Collections.unmodifiableSet(new HashSet<>(this.votedVoterIds));
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
