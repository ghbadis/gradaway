-- SQL script to update the candidature table
-- Run this script to add status and acceptance date columns to the candidature table

ALTER TABLE candidature ADD COLUMN status VARCHAR(20) DEFAULT 'pending';
ALTER TABLE candidature ADD COLUMN date_acceptation DATE DEFAULT NULL;

-- Optional: Update existing candidatures to have 'pending' status
UPDATE candidature SET status = 'pending' WHERE status IS NULL; 