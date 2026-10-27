// Package declaration for domain model entities
package com.polling.model;

// Import Date for receipt timestamping
import java.util.Date;

// Represents an immutable cryptographic receipt issued to a voter upon casting a ballot
public class VoteReceipt {

    // Private field storing the unique receipt verification code
    private final String receiptCode;

    // Private field storing the targeted poll ID
    private final String pollId;

    // Private field storing the voter's anonymized cryptographic SHA-256 hash
    private final String voterHash;

    // Private field storing the selected option
    private final String selectedOption;

    // Private field recording the exact timestamp when the vote was registered
    private final Date issuedAt;

    // Parameterized constructor initializing immutable receipt properties
    public VoteReceipt(String receiptCode, String pollId, String voterHash, String selectedOption) {
        // Assign receipt verification code
        this.receiptCode = receiptCode;
        // Assign target poll ID
        this.pollId = pollId;
        // Assign anonymized voter hash
        this.voterHash = voterHash;
        // Assign selected option
        this.selectedOption = selectedOption;
        // Record current system time
        this.issuedAt = new Date();
    // End of constructor
    }

    // Public getter for the receipt verification code
    public String getReceiptCode() {
        // Return receipt code
        return this.receiptCode;
    // End of method
    }

    // Public getter for target poll ID
    public String getPollId() {
        // Return poll ID
        return this.pollId;
    // End of method
    }

    // Public getter for voter SHA-256 hash
    public String getVoterHash() {
        // Return voter hash
        return this.voterHash;
    // End of method
    }

    // Public getter for selected option
    public String getSelectedOption() {
        // Return selected option
        return this.selectedOption;
    // End of method
    }

    // Public getter for issue timestamp
    public Date getIssuedAt() {
        // Return defensive copy of timestamp
        return new Date(this.issuedAt.getTime());
    // End of method
    }

    // Human-readable representation of receipt
    @Override
    public String toString() {
        // Format string showing receipt code and poll details
        return "VoteReceipt [" + receiptCode + " for " + pollId + " at " + issuedAt + "]";
    // End of method
    }
// End of class
}
