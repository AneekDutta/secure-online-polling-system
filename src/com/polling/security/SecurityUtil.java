//SHA 256 stands for Secure Hash Algorithm 256-bit. It is a cryptographic hash function
//  that converts input data into a fixed length hash value of 256 bits(32 bytes or 64 hexadecimal characters.)
//  it belongs to SHA-2 family of cryptographic hash algo.


//In an electronic voting system, there is a fundamental paradox:
//How do you guarantee that a voter votes only once, while ensuring that nobody (not even administrators or database operators) can link a voter's ballot to their real identity?
//SecurityUtil.java solves this using one-way SHA-256 cryptographic hashing with salting and peppering, coupled with tamper-evident receipt generation.

//CRYPTOGRAPHIC SECURITY UTILITY CLASS
//ROLE: Delivers SHA-256 salted voter pseudonymization and tamper-evident ballot receipt generation.
//KEY METHODS: hashVoterId (salting + peppering), generateReceiptCode (verifiable receipt), computeSha256Hex (cryptographic digest)


// Package declaration for cryptographic security utilities
package com.polling.security;

// Import MessageDigest for SHA-256 cryptographic hashing
import java.security.MessageDigest;
// Import NoSuchAlgorithmException for handling unsupported digest algorithms
import java.security.NoSuchAlgorithmException;
// Import StandardCharsets for deterministic UTF-8 byte encoding
import java.nio.charset.StandardCharsets;

// Security utility class delivering SHA-256 voter ID hashing and ballot receipt generation
public final class SecurityUtil {

    // Private constructor preventing instantiation of utility class
    private SecurityUtil() {
    // End of constructor
    }

    // Fixed application pepper string ensuring additional entropy
    private static final String SYSTEM_PEPPER = "SECURE_OOP_POLLING_2026_PEPPER"; //(CONSTANT)
                            //Pepper (SYSTEM_PEPPER): An application-level secret string combined with every hash.
                            //Even if an attacker obtains an offline dictionary / rainbow table for voter IDs, they cannot crack the hashes without this pepper.
    
    
    // Hashes a voter ID using SHA-256 with poll-specific salt to protect voter anonymity
    //Salt (pollId): Changes per poll. Ensures that the same voter voting in Poll #1 and Poll #2 
    //                produces completely different hashes, preventing cross-poll tracking

    public static String hashVoterId(String voterId, String pollId) {//Produces an irreversible, anonymized 64-character hex hash from a voter ID and a target poll.
        // Return empty string for null voter ID
        if (voterId == null) {
            // Null guard return
            return "";
        // End if
        }
        // Normalize voter ID text
        String cleanVoterId = voterId.trim().toUpperCase();
        // Normalize salt using poll ID and pepper
        String salt = (pollId != null) ? pollId.trim() : "DEFAULT_SALT";
        // Combine voter ID, salt, and pepper
        String combined = cleanVoterId + ":" + salt + ":" + SYSTEM_PEPPER;
        // Compute and return hexadecimal SHA-256 hash
        return computeSha256Hex(combined);

        //THIS IS USED IN: Poll.java: 
        // hasVoted() and recordVoteWithReceipt() check whether votedVoterHashes.contains(voterHash) in $O(1)$ time.
    // End of method
    }

    // Generates a verifiable ballot receipt code confirming successful vote recording
    public static String generateReceiptCode(String pollId, String voterHash, String option) {
        // Clean poll ID
        String cleanPoll = (pollId != null) ? pollId.trim() : "POLL";
        // Clean voter hash
        String cleanHash = (voterHash != null) ? voterHash.trim() : "HASH";
        // Clean option text
        String cleanOpt = (option != null) ? option.trim() : "OPT";
        // Combine parameters with timestamp
        String data = cleanPoll + "#" + cleanHash + "#" + cleanOpt + "#" + System.currentTimeMillis();
        // Compute SHA-256 digest
        String fullDigest = computeSha256Hex(data);
        // Truncate to 16 characters for readable receipt code format
        String shortCode = (fullDigest.length() >= 16) ? fullDigest.substring(0, 16).toUpperCase() : fullDigest.toUpperCase();
        // Return formatted receipt string
        return "RCPT-" + shortCode;
    // End of method
    }

    // Internal helper computing SHA-256 digest and formatting as uppercase hex string
    private static String computeSha256Hex(String input) {
        // Try block for SHA-256 MessageDigest lookup
        try {
            // Obtain SHA-256 MessageDigest instance from standard Java security provider
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Convert input string to UTF-8 byte array
            byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            // StringBuilder to assemble hex representation
            StringBuilder hexString = new StringBuilder();
            // Iterate over each byte in the digest array
            for (byte b : encodedhash) {
                // Convert byte to two-digit hex string
                String hex = Integer.toHexString(0xff & b);
                // Prepend leading zero if necessary
                if (hex.length() == 1) {
                    // Append 0
                    hexString.append('0');
                // End if
                }
                // Append hex characters
                hexString.append(hex);
            // End for
            }
            // Return assembled hex string in uppercase
            return hexString.toString().toUpperCase();
        // Catch NoSuchAlgorithmException in the unexpected case SHA-256 is missing
        } catch (NoSuchAlgorithmException e) {
            // Throw RuntimeException wrapping configuration fault
            throw new RuntimeException("SHA-256 algorithm not available in standard Java runtime", e);
        // End try-catch
        }
    // End of method
    }
// End of class
}
