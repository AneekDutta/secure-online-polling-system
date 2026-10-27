// Package declaration for event-driven observer system
package com.polling.event;

// Import Date for event timestamping
import java.util.Date;

// Immutable event payload dispatched to observers when poll state mutations occur
public class PollEvent {

    // Private field storing the category of event
    private final PollEventType eventType;

    // Private field storing the affected poll identifier
    private final String pollId;

    // Private field storing a human-readable event message
    private final String message;

    // Private field recording the exact moment of event creation
    private final Date timestamp;

    // Parameterized constructor initializing the event attributes
    public PollEvent(PollEventType eventType, String pollId, String message) {
        // Assign event type
        this.eventType = eventType;
        // Assign poll ID
        this.pollId = (pollId != null) ? pollId.trim() : "";
        // Assign message
        this.message = (message != null) ? message.trim() : "";
        // Record current timestamp
        this.timestamp = new Date();
    // End of constructor
    }

    // Public getter for event type
    public PollEventType getEventType() {
        // Return event type
        return this.eventType;
    // End of method
    }

    // Public getter for poll ID
    public String getPollId() {
        // Return poll ID
        return this.pollId;
    // End of method
    }

    // Public getter for descriptive event message
    public String getMessage() {
        // Return message
        return this.message;
    // End of method
    }

    // Public getter for event timestamp
    public Date getTimestamp() {
        // Return defensive copy of timestamp
        return new Date(this.timestamp.getTime());
    // End of method
    }

    // Readable string summary of event
    @Override
    public String toString() {
        // Format string showing event type and target poll
        return "PollEvent [" + eventType + " for " + pollId + " at " + timestamp + ": " + message + "]";
    // End of method
    }
// End of class
}
