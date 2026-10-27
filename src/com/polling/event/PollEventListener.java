// Package declaration for event-driven observer system
package com.polling.event;

// Observer interface implemented by components wishing to be notified of poll lifecycle events
public interface PollEventListener {

    // Callback method invoked when a poll event occurs in the system
    void onPollEvent(PollEvent event);
// End of interface
}
