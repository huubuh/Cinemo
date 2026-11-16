
BEGIN;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM movies
        WHERE age_rating IS NOT NULL AND age_rating NOT IN ('P', 'K', 'T13', 'T16', 'T18')
    ) THEN
        RAISE EXCEPTION 'Unsupported movies.age_rating values exist; review and correct them before rollout';
    END IF;
END $$;

ALTER TABLE movies ADD COLUMN IF NOT EXISTS active boolean NOT NULL DEFAULT true;
CREATE INDEX IF NOT EXISTS idx_movie_release_date ON movies (release_date);
CREATE INDEX IF NOT EXISTS idx_movie_end_date ON movies (end_date);

COMMIT;
