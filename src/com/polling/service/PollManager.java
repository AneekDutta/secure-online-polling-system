// Package declaration for service layer components
package com.polling.service;

// Import PollEvent for Observer pattern
import com.polling.event.PollEvent;
// Import PollEventListener interface
import com.polling.event.PollEventListener;
// Import PollEventType enum
import com.polling.event.PollEventType;
// Import custom domain exceptions
import com.polling.exception.PollNotFoundException;
// Import validation exception
import com.polling.exception.ValidationException;
// Import Poll domain model
import com.polling.model.Poll;
// Import User domain model
import com.polling.model.User;
// Import ArrayList
import java.util.ArrayList;
// Import Collections
import java.util.Collections;
// Import HashMap for poll storage catalog (demonstrating HashMap usage)
import java.util.HashMap;
// Import List interface
import java.util.List;
// Import Map interface
import java.util.Map;
// Import CopyOnWriteArrayList for thread-safe event listener dispatch
import java.util.concurrent.CopyOnWriteArrayList;
// Import AtomicInteger for generating consecutive poll numbers
import java.util.concurrent.atomic.AtomicInteger;

// Manages the lifecycle, storage, and catalog of polls while serving as the Subject in the Observer Pattern
public class PollManager {

    // Internal HashMap mapping poll ID strings to Poll objects
    private final Map<String, Poll> pollCatalog;

    // Sequence generator for generating incremental poll identifiers
    private final AtomicInteger idCounter;

    // Thread-safe collection of registered observers (Observer Pattern)
    private final List<PollEventListener> eventListeners;

    // Default constructor initializing in-memory collections and initial sample data
    public PollManager() {
        // Instantiate HashMap for fast poll lookup by ID
        this.pollCatalog = new HashMap<>();
        // Initialize counter starting at 101 for realistic poll identifiers
        this.idCounter = new AtomicInteger(101);
        // Initialize observer listeners list
        this.eventListeners = new CopyOnWriteArrayList<>();
        // Pre-populate realistic sample polls for immediate demonstration
        seedSamplePolls();
    // End of constructor
    }

    // Registers a listener for poll lifecycle notifications (Observer Pattern)
    public void addEventListener(PollEventListener listener) {
        // Validate listener presence
        if (listener != null && !this.eventListeners.contains(listener)) {
            // Add listener to collection
            this.eventListeners.add(listener);
        // End if
        }
    // End of method
    }

    // Unregisters a previously added listener (Observer Pattern)
    public void removeEventListener(PollEventListener listener) {
        // Remove listener
        if (listener != null) {
            // Remove from list
            this.eventListeners.remove(listener);
        // End if
        }
    // End of method
    }

    // Dispatches a poll event to all registered observers (Observer Pattern)
    public void notifyEvent(PollEvent event) {
        // Guard against null event
        if (event != null) {
            // Iterate over all active listeners
            for (PollEventListener listener : this.eventListeners) {
                // Try-catch block protecting dispatch loop from individual listener errors
                try {
                    // Invoke observer callback
                    listener.onPollEvent(event);
                // Catch any listener exception safely
                } catch (Exception ex) {
                    // Print dispatch warning
                    System.err.println("Warning: Listener failed during event dispatch: " + ex.getMessage());
                // End try-catch
                }
            // End for
            }
        // End if
        }
    // End of method
    }

    // Creates a new poll after validating question and options count
    public synchronized Poll createPoll(String question, List<String> rawOptions) throws ValidationException {
        // Validate that question string is not null or blank
        if (question == null || question.trim().isEmpty()) {
            // Throw validation exception when question is missing
            throw new ValidationException("Poll question cannot be empty.");
        // End if
        }
        // Validate options list presence
        if (rawOptions == null || rawOptions.isEmpty()) {
            // Throw validation exception when options list is missing
            throw new ValidationException("At least two valid options must be provided.");
        // End if
        }
        // List to hold cleaned, distinct options
        List<String> cleanOptions = new ArrayList<>();
        // Iterate through submitted options
        for (String opt : rawOptions) {
            // Verify option is non-null and non-blank
            if (opt != null && !opt.trim().isEmpty()) {
                // Trim option string
                String trimmed = opt.trim();
                // Check if option is already present to prevent duplicates within definition
                if (!cleanOptions.contains(trimmed)) {
                    // Add valid option to collection
                    cleanOptions.add(trimmed);
                // End if
                }
            // End if
            }
        // End for
        }
        // Enforce the rule requiring at least two distinct options
        if (cleanOptions.size() < 2) {
            // Throw validation exception if fewer than two distinct options exist
            throw new ValidationException("Poll must contain at least 2 distinct non-empty options.");
        // End if
        }
        // Generate new sequential poll ID
        String pollId = "POLL-" + idCounter.getAndIncrement();
        // Instantiate new Poll entity
        Poll newPoll = new Poll(pollId, question.trim(), cleanOptions);
        // Store poll into the HashMap catalog
        this.pollCatalog.put(pollId, newPoll);
        // Notify observers that a new poll was published
        notifyEvent(new PollEvent(PollEventType.POLL_CREATED, pollId, "Poll #" + pollId + " created: \"" + newPoll.getQuestion() + "\""));
        // Return the created poll
        return newPoll;
    // End of method
    }

    // Retrieves a poll by its unique poll ID from the HashMap
    public synchronized Poll getPoll(String pollId) throws PollNotFoundException {
        // Check for null or empty poll ID
        if (pollId == null || pollId.trim().isEmpty()) {
            // Throw exception for missing ID
            throw new PollNotFoundException("Poll ID must be specified.");
        // End if
        }
        // Clean poll ID string
        String cleanId = pollId.trim().toUpperCase();
        // Lookup poll in HashMap
        Poll poll = this.pollCatalog.get(cleanId);
        // If not found in catalog, throw custom exception
        if (poll == null) {
            // Throw descriptive exception
            throw new PollNotFoundException("Poll with ID '" + cleanId + "' does not exist.");
        // End if
        }
        // Return found poll
        return poll;
    // End of method
    }

    // Retrieves a snapshot list of all currently available polls
    public synchronized List<Poll> getAllPolls() {
        // Return a new ArrayList containing all Poll values currently in the HashMap
        return new ArrayList<>(this.pollCatalog.values());
    // End of method
    }

    // Closes a poll given its ID and verifying user administrative authority
    public synchronized void closePoll(String pollId, User user) throws PollNotFoundException, ValidationException {
        // Check if user object is present and authorized
        if (user == null || !user.canManagePolls()) {
            // Deny action if user lacks administrative role
            throw new ValidationException("Administrative permissions required to close this poll.");
        // End if
        }
        // Retrieve target poll (throws PollNotFoundException if not found)
        Poll poll = getPoll(pollId);
        // Call closePoll on target entity
        poll.closePoll();
        // Dispatch observer notification
        notifyEvent(new PollEvent(PollEventType.POLL_CLOSED, pollId, "Poll #" + pollId + " closed by admin " + user.getName()));
    // End of method
    }

    // Reopens a previously closed poll
    public synchronized void reopenPoll(String pollId, User user) throws PollNotFoundException, ValidationException {
        // Check if user object is present and authorized
        if (user == null || !user.canManagePolls()) {
            // Deny action if user lacks administrative role
            throw new ValidationException("Administrative permissions required to reopen this poll.");
        // End if
        }
        // Retrieve target poll
        Poll poll = getPoll(pollId);
        // Call openPoll on target entity
        poll.openPoll();
        // Dispatch observer notification
        notifyEvent(new PollEvent(PollEventType.POLL_REOPENED, pollId, "Poll #" + pollId + " reopened by admin " + user.getName()));
    // End of method
    }

    // Returns the total count of registered polls in memory
    public synchronized int getPollCount() {
        // Return size of the HashMap
        return this.pollCatalog.size();
    // End of method
    }

    // Pre-populates the in-memory catalog with sample polls for live demonstration
    private void seedSamplePolls() {
        // Create sample options for poll 1
        List<String> options1 = new ArrayList<>();
        // Add Java
        options1.add("Java (Object-Oriented, Platform Independent)");
        // Add Python
        options1.add("Python (Dynamic, High-level)");
        // Add C++
        options1.add("C++ (High Performance, System-level)");
        // Add C#
        options1.add("C# (Modern Enterprise, .NET)");
        // Instantiate sample poll 1
        Poll poll1 = new Poll("POLL-101", "What is your primary programming language for OOP?", options1);
        // Simulate initial votes in sample poll 1
        poll1.recordVote("VOTER-1001", "Java (Object-Oriented, Platform Independent)");
        // Record vote 2
        poll1.recordVote("VOTER-1002", "Java (Object-Oriented, Platform Independent)");
        // Record vote 3
        poll1.recordVote("VOTER-1003", "Python (Dynamic, High-level)");
        // Record vote 4
        poll1.recordVote("VOTER-1004", "C++ (High Performance, System-level)");
        // Store in catalog
        this.pollCatalog.put(poll1.getPollId(), poll1);

        // Create sample options for poll 2
        List<String> options2 = new ArrayList<>();
        // Add Campus Lab
        options2.add("Advanced Computing & AI Lab");
        // Add Library
        options2.add("Digital Library & Collaborative Spaces");
        // Add Sports
        options2.add("Indoor Sports Complex & Gymnasium");
        // Add Cafeteria
        options2.add("Student Food Court & Eco Cafeteria");
        // Instantiate sample poll 2
        Poll poll2 = new Poll("POLL-102", "Which campus facility needs the most urgent upgrade?", options2);
        // Simulate initial votes in sample poll 2 (balanced votes to demonstrate tie analysis)
        poll2.recordVote("VOTER-2001", "Advanced Computing & AI Lab");
        // Record vote 2
        poll2.recordVote("VOTER-2002", "Digital Library & Collaborative Spaces");
        // Record vote 3
        poll2.recordVote("VOTER-2003", "Advanced Computing & AI Lab");
        // Record vote 4
        poll2.recordVote("VOTER-2004", "Digital Library & Collaborative Spaces");
        // Store in catalog
        this.pollCatalog.put(poll2.getPollId(), poll2);

        // Create sample options for poll 3
        List<String> options3 = new ArrayList<>();
        // Option A
        options3.add("In-Person On Campus");
        // Option B
        options3.add("Fully Virtual / Online");
        // Option C
        options3.add("Hybrid Model (In-Person + Live Stream)");
        // Instantiate sample poll 3
        Poll poll3 = new Poll("POLL-103", "Preferred format for upcoming technical symposium?", options3);
        // Simulate sample vote
        poll3.recordVote("VOTER-3001", "Hybrid Model (In-Person + Live Stream)");
        // Record vote 2
        poll3.recordVote("VOTER-3002", "Hybrid Model (In-Person + Live Stream)");
        // Record vote 3
        poll3.recordVote("VOTER-3003", "In-Person On Campus");
        // Store in catalog
        this.pollCatalog.put(poll3.getPollId(), poll3);
    // End of method
    }
// End of class
}
