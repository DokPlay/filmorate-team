-- Ensure director tables exist with the same schema as the rest of the project.
-- Using IF NOT EXISTS keeps the migration idempotent when Flyway runs clean/migrate
-- in integration tests with an empty H2 database.

CREATE TABLE IF NOT EXISTS directors (
  director_id INTEGER AUTO_INCREMENT PRIMARY KEY,
  director_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_director (
  film_id INTEGER NOT NULL,
  director_id INTEGER NOT NULL,
  PRIMARY KEY (film_id, director_id),
  CONSTRAINT fk_fd_film
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
  CONSTRAINT fk_fd_director
    FOREIGN KEY (director_id) REFERENCES directors(director_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_fd_film     ON film_director (film_id);
CREATE INDEX IF NOT EXISTS idx_fd_director ON film_director (director_id);
