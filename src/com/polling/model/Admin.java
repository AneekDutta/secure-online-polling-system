// Package declaration for domain model entities
package com.polling.model;

// Admin user class extending User (demonstrating Inheritance and Polymorphism)
public class Admin extends User {

    // Private field storing the administrator's department or authority level
    private final String department;

    // Default constructor for standard administrative session
    public Admin(String userId, String name) {
        // Delegate to primary constructor with default department
        this(userId, name, "System Administration");
    // End of constructor
    }

    // Parameterized constructor initializing user attributes and department
    public Admin(String userId, String name, String department) {
        // Call superclass constructor to initialize inherited fields
        super(userId, name);
        // Initialize the department field
        this.department = (department != null) ? department.trim() : "General";
    // End of constructor
    }

    // Getter method for administrator department
    public String getDepartment() {
        // Return the department string
        return this.department;
    // End of method
    }

    // Overridden polymorphic method returning the Admin role
    @Override
    public String getRole() {
        // Return administrator role name
        return "ADMINISTRATOR";
    // End of method
    }

    // Overridden polymorphic method returning descriptive administrative permissions
    @Override
    public String getPermissionsDescription() {
        // Return detailed summary of administrative capabilities
        return "Full permissions: Create polls, close polls, view complete analytical results.";
    // End of method
    }

    // Overridden polymorphic method returning true for poll management capabilities
    @Override
    public boolean canManagePolls() {
        // Administrators are explicitly allowed to manage and close polls
        return true;
    // End of method
    }
// End of class
}
