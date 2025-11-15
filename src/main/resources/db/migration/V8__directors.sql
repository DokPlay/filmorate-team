-- Таблица режиссёров
CREATE TABLE IF NOT EXISTS directors (
  director_id BIGSERIAL PRIMARY KEY,
  name        VARCHAR(255) NOT NULL,
  created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Связь фильм—режиссёр (многие-ко-многим)
CREATE TABLE IF NOT EXISTS film_director (
  film_id     BIGINT NOT NULL,
  director_id BIGINT NOT NULL,
  PRIMARY KEY (film_id, director_id),
  CONSTRAINT fk_fd_film
    FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE,
  CONSTRAINT fk_fd_director
    FOREIGN KEY (director_id) REFERENCES directors(director_id) ON DELETE RESTRICT
);

-- Индексы для джоинов
CREATE INDEX IF NOT EXISTS idx_fd_film     ON film_director (film_id);
CREATE INDEX IF NOT EXISTS idx_fd_director ON film_director (director_id);
