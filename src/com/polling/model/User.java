//ABSTRACT BASE CLASS
//Role: Serves as the base class for all actors in the system. encapsultes shared identity fields: 1. userID & 2. name

// Package declaration for domain model entities
package com.polling.model;

// Abstract base class representing a user in the polling system (demonstrating Abstraction)
public abstract class User {

    // Private field storing the unique identifier of the user (demonstrating Encapsulation)
    private final String userId;

    // Private field storing the display name or handle of the user
    private final String name;

    // Parameterized constructor initializing the immutable user attributes
    public User(String userId, String name) {
        // Assign the unique identifier after trimming whitespace
        this.userId = (userId != null) ? userId.trim() : "";
        // Assign the display name after trimming whitespace
        this.name = (name != null) ? name.trim() : "";
    // End of constructor
    }

    // Public getter method for accessing the user ID
    public String getUserId() {
        // Return the encapsulated user ID
        return this.userId;
    // End of method
    }

    // Public getter method for accessing the user name
    public String getName() {
        // Return the encapsulated user name
        return this.name;
    // End of method
    }

    // Abstract method to get the role title of the user(ADMINISTRATOR OR VOTER) (implemented polymorphically)
    public abstract String getRole();

    // Abstract method returning a HUMAN READABLE description of user permissions
    public abstract String getPermissionsDescription();

    // Abstract method indicating whether this user type has administrative priviledges
    public abstract boolean canManagePolls();

    // Polymorphic toString implementation providing a readable representation
    @Override
    public String toString() {
        // Format and return string showing user details and role
        return getRole() + " [ID=" + userId + ", Name=" + name + "]";
    // End of method
    }
// End of class
}
