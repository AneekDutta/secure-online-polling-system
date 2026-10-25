// Package declaration for domain model entities
package com.polling.model;

// Voter user class extending User (demonstrating Inheritance and Polymorphism)
public class Voter extends User {

    // Parameterized constructor initializing the voter's credentials
    public Voter(String voterId, String name) {
        // Call superclass constructor to initialize inherited fields
        super(voterId, name);
    // End of constructor
    }

    // Overridden polymorphic method returning the Voter role
    @Override
    public String getRole() {
        // Return voter role name
        return "VOTER";
    // End of method
    }

    // Overridden polymorphic method returning descriptive voter permissions
    @Override
    public String getPermissionsDescription() {
        // Return detailed summary of voter capabilities
        return "Standard permissions: View open polls, cast a single vote per poll, review results.";
    // End of method
    }

    // Overridden polymorphic method returning false for poll management capabilities
    @Override
    public boolean canManagePolls() {
        // Regular voters are not permitted to close or administratively modify polls
        return false;
    // End of method
    }
// End of class
}
