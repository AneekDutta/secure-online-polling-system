// Package declaration for database and cloud persistence components
package com.polling.database;

// Import Poll domain model
import com.polling.model.Poll;
// Import VoteReceipt domain model
import com.polling.model.VoteReceipt;
// Import URI for network endpoint parsing
import java.net.URI;
// Import HttpClient for executing non-blocking HTTP requests
import java.net.http.HttpClient;
// Import HttpRequest for constructing HTTP payloads
import java.net.http.HttpRequest;
// Import HttpResponse for parsing HTTP return bodies
import java.net.http.HttpResponse;
// Import Duration for timeout configuration
import java.time.Duration;
// Import CompletableFuture for asynchronous non-blocking cloud persistence
import java.util.concurrent.CompletableFuture;

// Lightweight pure-Java client synchronizing polls, voter hashes, and receipts with Supabase PostgreSQL
public class SupabaseClient {

    // Singleton standard Java HTTP client instance
    private final HttpClient httpClient;

    // Timeout duration for cloud network requests
    private static final Duration TIMEOUT = Duration.ofSeconds(4);

    // Default constructor initializing standard Java HttpClient
    public SupabaseClient() {
        // Instantiate standard Java HttpClient with connection timeout
        this.httpClient = HttpClient.newBuilder()
            // Set connection timeout
            .connectTimeout(TIMEOUT)
            // Build client instance
            .build();
    // End of constructor
    }

    // Checks whether the Supabase cloud database is reachable
    public boolean isAvailable() {
        // Try block for network connectivity test
        try {
            // Build lightweight HEAD or GET request to the polls endpoint
            HttpRequest request = HttpRequest.newBuilder()
                // Target URL
                .uri(URI.create(SupabaseConfig.ENDPOINT_POLLS + "?select=id&limit=1"))
                // Request timeout
                .timeout(TIMEOUT)
                // Add Supabase API key header
                .header("apikey", SupabaseConfig.SUPABASE_ANON_KEY)
                // Add Authorization header
                .header("Authorization", "Bearer " + SupabaseConfig.SUPABASE_ANON_KEY)
                // GET method
                .GET()
                // Build request
                .build();
            // Send synchronous request
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // Return true if HTTP status code is 200 OK
            return response.statusCode() == 200;
        // Catch network or connection exceptions gracefully
        } catch (Exception e) {
            // Cloud not available
            return false;
        // End try-catch
        }
    // End of method
    }

    // Asynchronously logs an audit lifecycle event to Supabase PostgreSQL table 'audit_events'
    public CompletableFuture<Boolean> logAuditEventAsync(String eventType, String pollId, String message) {
        // Build JSON body string
        String json = "{\"event_type\":\"" + escapeJson(eventType) + "\","
                    + "\"poll_id\":\"" + escapeJson(pollId) + "\","
                    + "\"message\":\"" + escapeJson(message) + "\"}";
        // Dispatch asynchronous POST request
        return postJsonAsync(SupabaseConfig.ENDPOINT_AUDIT_EVENTS, json);
    // End of method
    }

    // Asynchronously persists a new poll and its options to Supabase tables 'polls' and 'poll_options'
    public CompletableFuture<Boolean> syncPollAsync(Poll poll) {
        // Guard against null poll
        if (poll == null) {
            // Return completed future with false
            return CompletableFuture.completedFuture(false);
        // End if
        }
        // Build JSON payload for poll record
        String pollJson = "{\"id\":\"" + escapeJson(poll.getPollId()) + "\","
                        + "\"question\":\"" + escapeJson(poll.getQuestion()) + "\","
                        + "\"is_open\":" + poll.isOpen() + "}";

        // Send poll insert first
        return postJsonAsync(SupabaseConfig.ENDPOINT_POLLS, pollJson).thenCompose(success -> {
            // If poll insert succeeded
            if (success) {
                // Build options JSON array
                StringBuilder optArray = new StringBuilder("[");
                // Option counter
                int count = 0;
                // Iterate through poll options
                for (String opt : poll.getOptions()) {
                    // Comma separator
                    if (count > 0) optArray.append(",");
                    // Append option object
                    optArray.append("{\"poll_id\":\"").append(escapeJson(poll.getPollId())).append("\",")
                            .append("\"option_text\":\"").append(escapeJson(opt)).append("\",")
                            .append("\"vote_count\":").append(poll.getVoteCountForOption(opt)).append("}");
                    // Increment count
                    count++;
                // End for
                }
                // Close array
                optArray.append("]");
                // Send options batch insert
                return postJsonAsync(SupabaseConfig.ENDPOINT_OPTIONS, optArray.toString());
            // If poll insert failed
            } else {
                // Return false
                return CompletableFuture.completedFuture(false);
            // End if
            }
        // End thenCompose
        });
    // End of method
    }

    // Asynchronously records a cast ballot into Supabase 'voter_hashes' and 'vote_receipts' tables
    public CompletableFuture<Boolean> recordBallotAsync(String pollId, String voterHash, String selectedOption, VoteReceipt receipt) {
        // Guard against missing parameters
        if (pollId == null || voterHash == null || selectedOption == null || receipt == null) {
            // Return false future
            return CompletableFuture.completedFuture(false);
        // End if
        }

        // 1. JSON payload for voter hash (enforces single-voting constraint in PostgreSQL)
        String hashJson = "{\"poll_id\":\"" + escapeJson(pollId) + "\","
                        + "\"voter_hash\":\"" + escapeJson(voterHash) + "\"}";

        // 2. JSON payload for vote receipt
        String receiptJson = "{\"receipt_code\":\"" + escapeJson(receipt.getReceiptCode()) + "\","
                           + "\"poll_id\":\"" + escapeJson(pollId) + "\","
                           + "\"voter_hash\":\"" + escapeJson(voterHash) + "\","
                           + "\"selected_option\":\"" + escapeJson(selectedOption) + "\"}";

        // Execute voter hash insert followed by receipt insert
        return postJsonAsync(SupabaseConfig.ENDPOINT_VOTER_HASHES, hashJson).thenCompose(hashOk -> {
            // If hash insert succeeded
            if (hashOk) {
                // Insert receipt record
                return postJsonAsync(SupabaseConfig.ENDPOINT_VOTE_RECEIPTS, receiptJson);
            // If hash failed (duplicate in DB)
            } else {
                // Return false
                return CompletableFuture.completedFuture(false);
            // End if
            }
        // End thenCompose
        });
    // End of method
    }

    // Helper method performing asynchronous HTTP POST of JSON payload to a specified Supabase endpoint
    private CompletableFuture<Boolean> postJsonAsync(String endpoint, String jsonPayload) {
        // Try block for request formulation
        try {
            // Build HTTP POST request
            HttpRequest request = HttpRequest.newBuilder()
                // Target endpoint URI
                .uri(URI.create(endpoint))
                // Request timeout
                .timeout(TIMEOUT)
                // Supabase API key
                .header("apikey", SupabaseConfig.SUPABASE_ANON_KEY)
                // Authorization token
                .header("Authorization", "Bearer " + SupabaseConfig.SUPABASE_ANON_KEY)
                // Content-Type header
                .header("Content-Type", "application/json")
                // Return representation preference
                .header("Prefer", "return=minimal")
                // POST body
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                // Build request
                .build();

            // Send request asynchronously via HttpClient
            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                // Process response
                .thenApply(res -> {
                    // Return true if response is 200 OK or 201 Created
                    return res.statusCode() == 200 || res.statusCode() == 201;
                // End lambda
                })
                // Handle exceptional completion gracefully
                .exceptionally(ex -> {
                    // Return false on network or connection errors
                    return false;
                // End lambda
                });
        // Catch synchronous preparation exceptions
        } catch (Exception e) {
            // Return false future
            return CompletableFuture.completedFuture(false);
        // End try-catch
        }
    // End of method
    }

    // Helper sanitizing strings for JSON payload injection
    private static String escapeJson(String input) {
        // Guard against null
        if (input == null) return "";
        // Replace backslashes and double quotes
        return input.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    // End of method
    }
// End of class
}
