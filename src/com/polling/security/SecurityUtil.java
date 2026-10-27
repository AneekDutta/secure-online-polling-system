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
    private static final String SYSTEM_PEPPER = "SECURE_OOP_POLLING_2026_PEPPER";

    // Hashes a voter ID using SHA-256 with poll-specific salt to protect voter anonymity
    public static String hashVoterId(String voterId, String pollId) {
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
