-- Базовая схема для Filmorate (пользователи, фильмы, лайки)

-- Пользователи
CREATE TABLE IF NOT EXISTS users (
    id        BIGSERIAL PRIMARY KEY,
    email     VARCHAR(255) NOT NULL UNIQUE,
    login     VARCHAR(255) NOT NULL,
    name      VARCHAR(255) NOT NULL,
    birthday  DATE         NOT NULL
);

-- Фильмы
CREATE TABLE IF NOT EXISTS films (
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(255)  NOT NULL,
    description  VARCHAR(1000),
    release_date DATE          NOT NULL,
    duration     INTEGER       NOT NULL CHECK (duration > 0)
);

-- Лайки «пользователь — фильм»
CREATE TABLE IF NOT EXISTS likes (
    user_id BIGINT NOT NULL,
    film_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, film_id),
    CONSTRAINT fk_likes_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_likes_film
        FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_likes_user ON likes (user_id);
CREATE INDEX IF NOT EXISTS idx_likes_film ON likes (film_id);
