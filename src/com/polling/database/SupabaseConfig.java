// Package declaration for database and cloud persistence components
package com.polling.database;

// Central configuration constants for Supabase PostgreSQL REST integration
public final class SupabaseConfig {

    // Private constructor preventing instantiation of utility constants class
    private SupabaseConfig() {
    // End of constructor
    }

    // Base URL of the Supabase project
    public static final String SUPABASE_URL = "https://ykxcoihvfzgykrkabbdy.supabase.co";

    // Standard publishable API key for authenticating REST requests against Supabase PostgREST gateway
    public static final String SUPABASE_ANON_KEY = "sb_publishable_9zGVF54qxH6SeLrAU1gpWw_aFN_lMhh";

    // REST endpoint URL for querying and managing polls
    public static final String ENDPOINT_POLLS = SUPABASE_URL + "/rest/v1/polls";

    // REST endpoint URL for querying and inserting poll candidate options
    public static final String ENDPOINT_OPTIONS = SUPABASE_URL + "/rest/v1/poll_options";

    // REST endpoint URL for persisting cryptographic voter hashes (PostgreSQL unique constraint)
    public static final String ENDPOINT_VOTER_HASHES = SUPABASE_URL + "/rest/v1/voter_hashes";

    // REST endpoint URL for persisting and auditing ballot receipts
    public static final String ENDPOINT_VOTE_RECEIPTS = SUPABASE_URL + "/rest/v1/vote_receipts";

    // REST endpoint URL for recording system audit events
    public static final String ENDPOINT_AUDIT_EVENTS = SUPABASE_URL + "/rest/v1/audit_events";
// End of class
}
