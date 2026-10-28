-- ====================================================================
-- SECURE ONLINE POLLING SYSTEM - COMPLETE DATABASE SCHEMA (ITERATION 2)
-- Run this script in the Supabase SQL Editor of your fresh project.
-- ====================================================================

-- 1. Create Polls Table
CREATE TABLE IF NOT EXISTS public.polls (
    id TEXT PRIMARY KEY,
    question TEXT NOT NULL,
    created_by TEXT NOT NULL,
    is_open BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

-- 2. Create Candidate Options Table
CREATE TABLE IF NOT EXISTS public.poll_options (
    id BIGSERIAL PRIMARY KEY,
    poll_id TEXT NOT NULL REFERENCES public.polls(id) ON DELETE CASCADE,
    option_text TEXT NOT NULL,
    vote_count INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT unique_poll_option UNIQUE (poll_id, option_text)
);

-- 3. Create Salted Voter Hashes Table (O(1) Unique Constraint Collision Defense)
CREATE TABLE IF NOT EXISTS public.voter_hashes (
    id BIGSERIAL PRIMARY KEY,
    poll_id TEXT NOT NULL REFERENCES public.polls(id) ON DELETE CASCADE,
    voter_hash TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now()),
    CONSTRAINT unique_voter_poll_hash UNIQUE (poll_id, voter_hash)
);

-- 4. Create Tamper-Evident Vote Receipts Table
CREATE TABLE IF NOT EXISTS public.vote_receipts (
    id BIGSERIAL PRIMARY KEY,
    receipt_code TEXT NOT NULL UNIQUE,
    poll_id TEXT NOT NULL REFERENCES public.polls(id) ON DELETE CASCADE,
    voter_hash TEXT NOT NULL,
    option_choice TEXT NOT NULL,
    issued_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

-- 5. Create System Audit Events Table
CREATE TABLE IF NOT EXISTS public.audit_events (
    id BIGSERIAL PRIMARY KEY,
    event_type TEXT NOT NULL,
    entity_id TEXT,
    details TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

-- ====================================================================
-- ROW-LEVEL SECURITY (RLS) POLICIES
-- Allows desktop application with standard anon key to read/insert
-- ====================================================================

ALTER TABLE public.polls ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.poll_options ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.voter_hashes ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.vote_receipts ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.audit_events ENABLE ROW LEVEL SECURITY;

DO $$ 
BEGIN
    DROP POLICY IF EXISTS "Public Read Polls" ON public.polls;
    CREATE POLICY "Public Read Polls" ON public.polls FOR SELECT USING (true);
    
    DROP POLICY IF EXISTS "Public Insert Polls" ON public.polls;
    CREATE POLICY "Public Insert Polls" ON public.polls FOR INSERT WITH CHECK (true);
    
    DROP POLICY IF EXISTS "Public Update Polls" ON public.polls;
    CREATE POLICY "Public Update Polls" ON public.polls FOR UPDATE USING (true);

    DROP POLICY IF EXISTS "Public Read Options" ON public.poll_options;
    CREATE POLICY "Public Read Options" ON public.poll_options FOR SELECT USING (true);
    
    DROP POLICY IF EXISTS "Public Insert Options" ON public.poll_options;
    CREATE POLICY "Public Insert Options" ON public.poll_options FOR INSERT WITH CHECK (true);
    
    DROP POLICY IF EXISTS "Public Update Options" ON public.poll_options;
    CREATE POLICY "Public Update Options" ON public.poll_options FOR UPDATE USING (true);

    DROP POLICY IF EXISTS "Public Read Hashes" ON public.voter_hashes;
    CREATE POLICY "Public Read Hashes" ON public.voter_hashes FOR SELECT USING (true);
    
    DROP POLICY IF EXISTS "Public Insert Hashes" ON public.voter_hashes;
    CREATE POLICY "Public Insert Hashes" ON public.voter_hashes FOR INSERT WITH CHECK (true);

    DROP POLICY IF EXISTS "Public Read Receipts" ON public.vote_receipts;
    CREATE POLICY "Public Read Receipts" ON public.vote_receipts FOR SELECT USING (true);
    
    DROP POLICY IF EXISTS "Public Insert Receipts" ON public.vote_receipts;
    CREATE POLICY "Public Insert Receipts" ON public.vote_receipts FOR INSERT WITH CHECK (true);

    DROP POLICY IF EXISTS "Public Read Audits" ON public.audit_events;
    CREATE POLICY "Public Read Audits" ON public.audit_events FOR SELECT USING (true);
    
    DROP POLICY IF EXISTS "Public Insert Audits" ON public.audit_events;
    CREATE POLICY "Public Insert Audits" ON public.audit_events FOR INSERT WITH CHECK (true);
END $$;

-- ====================================================================
-- INITIAL DEMONSTRATION SEED DATA
-- ====================================================================

INSERT INTO public.polls (id, question, created_by, is_open)
VALUES 
    ('POLL-101', 'What is your primary programming language for OOP?', 'ADMIN-101', true),
    ('POLL-102', 'Which campus facility needs the most urgent upgrade?', 'ADMIN-101', true),
    ('POLL-103', 'Preferred format for upcoming technical symposium?', 'ADMIN-101', true)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.poll_options (poll_id, option_text, vote_count)
VALUES
    ('POLL-101', 'Java (Core Standard SE)', 3),
    ('POLL-101', 'C++ (Standard ISO)', 1),
    ('POLL-101', 'Python (OOP Classes)', 0),
    ('POLL-101', 'Kotlin / Swift', 0),
    ('POLL-102', 'Central Computing Laboratories', 2),
    ('POLL-102', 'Campus Library & Study Halls', 1),
    ('POLL-102', 'Student Cafeteria & Food Court', 1),
    ('POLL-102', 'Indoor Sports Complex', 0),
    ('POLL-103', 'In-Person Hands-on Workshops', 2),
    ('POLL-103', 'Virtual / Hybrid Livestream', 1),
    ('POLL-103', '24-Hour Hackathon Format', 0)
ON CONFLICT (poll_id, option_text) DO NOTHING;

INSERT INTO public.audit_events (event_type, entity_id, details)
VALUES 
    ('PROJECT_INITIALIZED', 'SYSTEM', 'Fresh Supabase Cloud Database initialized with Clean OOP Schema.')
ON CONFLICT DO NOTHING;
