-- Ensure date fields match Java mappers that always expect non-null values
-- Fill missing values before tightening constraints
UPDATE users SET birthday = DATE '1970-01-01' WHERE birthday IS NULL;
UPDATE films SET release_date = DATE '1970-01-01' WHERE release_date IS NULL;

ALTER TABLE users
    ALTER COLUMN birthday SET NOT NULL;

ALTER TABLE films
    ALTER COLUMN release_date SET NOT NULL;
