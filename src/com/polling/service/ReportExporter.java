//ROLE:  File I/O Exporter (CSV & Certified Audit Reports)
//WHAT IT DOES:-
//1. CSV Export (exportToCSV):
//            -Generates RFC-4180 compliant CSV files with headers, metadata comments, option names, vote counts, and percentage shares.
//            -Uses escapeCsv() to safely handle option names with commas or quotes.
//2. Official Audit Export (exportCertifiedAuditReport):
//            -Uses Java BufferedWriter to write a formatted .txt report.
//            -Includes official election certification banners, timestamps, outcome verdicts, and candidate breakdown tables.
//            -Audit Trail: Prints the list of SHA-256 hashed voter IDs stored in the poll's HashSet.
//            -This proves each ballot came from a distinct voter while guaranteeing voter ballot secrecy (plain-text IDs are never revealed).


// Package declaration for service layer components
package com.polling.service;

// Import Poll domain model
import com.polling.model.Poll;
// Import File for file I/O operations
import java.io.File;
// Import FileWriter for character stream writing
import java.io.FileWriter;
// Import BufferedWriter for buffered text output
import java.io.BufferedWriter;
// Import IOException for handling write errors
import java.io.IOException;
// Import SimpleDateFormat for clean date formatting
import java.text.SimpleDateFormat;
// Import Date for timestamps
import java.util.Date;
// Import Map
import java.util.Map;
// Import Set
import java.util.Set;

// Utility service handling automated CSV and formatted audit report exports using Java File I/O
public class ReportExporter {

    // Date formatter for export timestamps
    private static final SimpleDateFormat REPORT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

    // Exports poll result metrics as a standard Comma-Separated Values (CSV) file
    public static void exportToCSV(PollResultSummary summary, File file) throws IOException {
        // Validate summary parameter
        if (summary == null) {
            // Guard throw
            throw new IllegalArgumentException("PollResultSummary cannot be null.");
        // End if
        }
        // Validate file parameter
        if (file == null) {
            // Guard throw
            throw new IllegalArgumentException("Target file cannot be null.");
        // End if
        }

        // Try-with-resources ensuring BufferedWriter is closed reliably
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Write CSV metadata header row
            writer.write("# Poll ID: " + escapeCsv(summary.getPollId()));
            // Newline
            writer.newLine();
            // Write question row
            writer.write("# Question: " + escapeCsv(summary.getQuestion()));
            // Newline
            writer.newLine();
            // Write status row
            writer.write("# Status: " + (summary.isOpen() ? "OPEN" : "CLOSED"));
            // Newline
            writer.newLine();
            // Write total votes row
            writer.write("# Total Votes: " + summary.getTotalVotes());
            // Newline
            writer.newLine();
            // Write strategy verdict
            writer.write("# Verdict: " + escapeCsv(summary.getWinnerDescription()));
            // Newline
            writer.newLine();
            // Write empty line separator
            writer.newLine();
            // Write CSV column headers
            writer.write("Option,Vote Count,Percentage (%)");
            // Newline
            writer.newLine();

            // Extract option counts map
            Map<String, Integer> counts = summary.getOptionCounts();
            // Extract option percentages map
            Map<String, Double> percentages = summary.getOptionPercentages();

            // Iterate over all options
            for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                // Option text
                String opt = entry.getKey();
                // Vote tally
                int count = entry.getValue();
                // Percentage value
                double pct = percentages.getOrDefault(opt, 0.0);
                // Format CSV row
                String row = escapeCsv(opt) + "," + count + "," + String.format("%.1f", pct);
                // Write row
                writer.write(row);
                // Newline
                writer.newLine();
            // End for
            }
            // Flush buffer to disk
            writer.flush();
        // End try-with-resources
        }
    // End of method
    }

    // Exports a certified, human-readable plain text audit report including SHA-256 voter hashes
    public static void exportCertifiedAuditReport(Poll poll, PollResultSummary summary, File file) throws IOException {
        // Validate poll presence
        if (poll == null) {
            // Guard throw
            throw new IllegalArgumentException("Poll cannot be null.");
        // End if
        }
        // Validate summary presence
        if (summary == null) {
            // Guard throw
            throw new IllegalArgumentException("Summary cannot be null.");
        // End if
        }
        // Validate file presence
        if (file == null) {
            // Guard throw
            throw new IllegalArgumentException("File cannot be null.");
        // End if
        }

        // Try-with-resources for buffered writing
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Write banner line
            writer.write("================================================================================");
            // Newline
            writer.newLine();
            // Write report title
            writer.write("        OFFICIAL ELECTION AUDIT REPORT & VOTE CERTIFICATION RECORD");
            // Newline
            writer.newLine();
            // Write banner line
            writer.write("================================================================================");
            // Newline
            writer.newLine();
            // Write generation timestamp
            writer.write("Generated At       : " + REPORT_DATE_FORMAT.format(new Date()));
            // Newline
            writer.newLine();
            // Write Poll ID
            writer.write("Poll Identifier    : #" + poll.getPollId());
            // Newline
            writer.newLine();
            // Write Question
            writer.write("Question Prompt    : " + poll.getQuestion());
            // Newline
            writer.newLine();
            // Write Status
            writer.write("Lifecycle Status   : " + (poll.isOpen() ? "OPEN (Accepting Votes)" : "CLOSED (Ballots Locked)"));
            // Newline
            writer.newLine();
            // Write Total Votes
            writer.write("Total Votes Cast   : " + summary.getTotalVotes());
            // Newline
            writer.newLine();
            // Write Unique Voters count
            writer.write("Unique Voter Hashes: " + poll.getVoterCount());
            // Newline
            writer.newLine();
            // Write Evaluation verdict
            writer.write("Calculated Outcome : " + summary.getWinnerDescription());
            // Newline
            writer.newLine();
            // Write divider
            writer.write("--------------------------------------------------------------------------------");
            // Newline
            writer.newLine();
            // Subheading
            writer.write("DETAILED OPTION BREAKDOWN:");
            // Newline
            writer.newLine();
            // Column headers
            writer.write(String.format("%-45s | %-12s | %-12s", "Option Candidate", "Vote Tally", "Percentage"));
            // Newline
            writer.newLine();
            // Header underline
            writer.write("----------------------------------------------+--------------+------------------");
            // Newline
            writer.newLine();

            // Extract counts map
            Map<String, Integer> counts = summary.getOptionCounts();
            // Extract percentages map
            Map<String, Double> percentages = summary.getOptionPercentages();

            // Iterate over options
            for (String opt : poll.getOptions()) {
                // Count
                int count = counts.getOrDefault(opt, 0);
                // Percentage
                double pct = percentages.getOrDefault(opt, 0.0);
                // Truncate option name if too long
                String displayName = (opt.length() > 44) ? opt.substring(0, 41) + "..." : opt;
                // Format line
                writer.write(String.format("%-45s | %-12d | %6.1f%%", displayName, count, pct));
                // Newline
                writer.newLine();
            // End for
            }

            // Write divider
            writer.write("--------------------------------------------------------------------------------");
            // Newline
            writer.newLine();
            // Subheading for cryptographic hashes
            writer.write("CRYPTOGRAPHIC VOTER AUDIT TRAIL (SHA-256 SALT HASHES IN HASHSET):");
            // Newline
            writer.newLine();
            // Explanation
            writer.write("Note: Plain-text Voter IDs are never stored to preserve voter ballot privacy.");
            // Newline
            writer.newLine();

            // Retrieve voter hashes
            Set<String> voterHashes = poll.getVotedVoterHashes();
            // If empty
            if (voterHashes.isEmpty()) {
                // Write no votes line
                writer.write("  [No voter hashes recorded yet]");
                // Newline
                writer.newLine();
            // If hashes exist
            } else {
                // Counter
                int idx = 1;
                // Iterate over hashes
                for (String hash : voterHashes) {
                    // Write numbered hash
                    writer.write(String.format("  %3d. %s", idx++, hash));
                    // Newline
                    writer.newLine();
                // End for
                }
            // End if
            }

            // Write divider
            writer.write("================================================================================");
            // Newline
            writer.newLine();
            // Footer notice
            writer.write("End of Certified Audit Report. Verification System: Secure Polling Iteration 2.");
            // Newline
            writer.newLine();
            // Flush buffer
            writer.flush();
        // End try-with-resources
        }
    // End of method
    }

    // Helper escaping quotes and commas for RFC-4180 CSV compliance
    private static String escapeCsv(String input) {
        // Guard against null
        if (input == null) {
            // Empty string
            return "";
        // End if
        }
        // If string contains comma, quote, or newline
        if (input.contains(",") || input.contains("\"") || input.contains("\n")) {
            // Replace internal quotes with double quotes and wrap in quotes
            return "\"" + input.replace("\"", "\"\"") + "\"";
        // End if
        }
        // Return unchanged if no special characters
        return input;
    // End of method
    }
// End of class
}
