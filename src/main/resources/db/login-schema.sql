-- Existing local databases may predate JWT version-based revocation.
-- Keep existing accounts and initialize their first token version to zero.
ALTER TABLE accounts
    ADD COLUMN IF NOT EXISTS token_version integer NOT NULL DEFAULT 0;
