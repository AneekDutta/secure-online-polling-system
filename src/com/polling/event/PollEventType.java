// Package declaration for event-driven observer system
package com.polling.event;

// Enumeration of lifecycle events dispatched by the polling system (demonstrating Observer Pattern)
public enum PollEventType {

    // Fired when a voter successfully casts a verified ballot in a poll
    VOTE_CAST,

    // Fired when a new poll is created and published to the catalog
    POLL_CREATED,

    // Fired when an administrator closes a poll, blocking subsequent votes
    POLL_CLOSED,

    // Fired when an administrator reopens a previously closed poll
    POLL_REOPENED
// End of enum
}
