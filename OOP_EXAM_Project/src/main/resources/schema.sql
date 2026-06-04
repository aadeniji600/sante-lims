-- ================================================================
-- schema.sql
-- Santé Diagnostics LIMS — Complete Database Schema
--
-- HOW TO RUN: Open pgAdmin, connect to your PostgreSQL server,
-- create a database called 'lims_db', then run this entire file.
--
-- IMPORTANT: Run this ONCE. Running it again will fail because
-- the tables already exist (that's what IF NOT EXISTS prevents).
-- ================================================================

-- ----------------------------------------------------------------
-- USERS TABLE
-- Stores all three user types (Super Admin, Lab Attendant, Customer)
-- The 'role' column determines which dashboard they see after login
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id            SERIAL PRIMARY KEY,          -- auto-incremented ID
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE, -- must be unique
    password_hash VARCHAR(255) NOT NULL,        -- BCrypt hash, NEVER plain text
    role          VARCHAR(20)  NOT NULL,        -- SUPER_ADMIN, LAB_ATTENDANT, CUSTOMER
    is_first_login BOOLEAN     DEFAULT TRUE,   -- TRUE = must change password on login
    is_verified   BOOLEAN      DEFAULT FALSE,  -- TRUE = email has been verified
    created_at    TIMESTAMP    DEFAULT NOW()
);

-- ----------------------------------------------------------------
-- TEST_TYPES TABLE
-- Defined by Super Admin (e.g. "Blood Count", "X-Ray Imaging")
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS test_types (
    id             SERIAL PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    price          DECIMAL(10, 2) NOT NULL,      -- e.g. 5000.00 (Naira)
    tat_hours      INT NOT NULL,                  -- Turnaround time in hours
    result_format  VARCHAR(20) NOT NULL,          -- NUMERIC, TEXT, PDF, IMAGE
    created_by     INT REFERENCES users(id),      -- which Super Admin created it
    created_at     TIMESTAMP DEFAULT NOW()
);

-- ----------------------------------------------------------------
-- TEST_REQUESTS TABLE
-- Created when a Customer orders a test
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS test_requests (
    id              SERIAL PRIMARY KEY,
    customer_id     INT REFERENCES users(id) NOT NULL,
    test_type_id    INT REFERENCES test_types(id) NOT NULL,
    payment_status  VARCHAR(10) DEFAULT 'UNPAID',  -- PAID or UNPAID
    requested_at    TIMESTAMP DEFAULT NOW(),
    expected_ready_at TIMESTAMP                    -- calculated from TAT
);

-- ----------------------------------------------------------------
-- SAMPLES TABLE
-- Tracks the physical sample through the lab process
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS samples (
    id          SERIAL PRIMARY KEY,
    request_id  INT REFERENCES test_requests(id) NOT NULL,
    status      VARCHAR(20) DEFAULT 'COLLECTED',  -- COLLECTED, PROCESSING, VALIDATED
    collected_at  TIMESTAMP DEFAULT NOW(),
    processed_at  TIMESTAMP,
    validated_at  TIMESTAMP
);

-- ----------------------------------------------------------------
-- RESULTS TABLE
-- Holds the final test result, attached to a sample
-- is_validated must be TRUE before the customer can see it
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS results (
    id            SERIAL PRIMARY KEY,
    sample_id     INT REFERENCES samples(id) NOT NULL,
    file_url      VARCHAR(500),                  -- path to PDF or image file
    result_text   TEXT,                          -- for NUMERIC or TEXT results
    is_validated  BOOLEAN DEFAULT FALSE,
    validated_by  INT REFERENCES users(id),      -- which Lab Attendant validated it
    validated_at  TIMESTAMP,
    uploaded_at   TIMESTAMP DEFAULT NOW()
);

-- ----------------------------------------------------------------
-- AUDIT_LOG TABLE
-- Immutable log — no UPDATE or DELETE should ever be run on this table
-- Records every important action in the system
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS audit_log (
    id          SERIAL PRIMARY KEY,
    user_id     INT REFERENCES users(id),
    action      TEXT NOT NULL,            -- e.g. "Marked request #12 as PAID"
    entity_type VARCHAR(50),              -- e.g. "test_request", "result"
    entity_id   INT,                      -- the ID of the thing that was acted on
    logged_at   TIMESTAMP DEFAULT NOW()
);

-- ----------------------------------------------------------------
-- Default Super Admin account (password: Admin@123)
-- The BCrypt hash below is for "Admin@123"
-- They will be forced to change it on first login
-- ----------------------------------------------------------------
INSERT INTO users (name, email, password_hash, role, is_first_login, is_verified)
VALUES (
    'System Admin',
    'admin@santediagnostics.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'SUPER_ADMIN',
    TRUE,
    TRUE
) ON CONFLICT DO NOTHING; -- skip if already inserted
