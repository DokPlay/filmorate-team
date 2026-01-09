-- Оптимизация схемы: индексы для частых выборок и ограничения уникальности
-- Добавляем индексы по внешним ключам и уникальные ограничения, чтобы ускорить поиск и убрать дубликаты

-- Уникальность почты и логина пользователей для 3НФ и корректной аутентификации
ALTER TABLE users
    ADD CONSTRAINT IF NOT EXISTS uq_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT IF NOT EXISTS uq_users_login UNIQUE (login);

-- Индекс по рейтингу ассоциации фильма для ускорения join с таблицей mpa
CREATE INDEX IF NOT EXISTS idx_films_mpa
    ON films (mpa_id);

-- Дополнительный индекс по жанру, чтобы ускорить выборки фильмов по жанрам
CREATE INDEX IF NOT EXISTS idx_film_genre_genre
    ON film_genre (genre_id);

-- Индекс по пользователю, ставящему лайк, чтобы быстро находить все лайки пользователя
CREATE INDEX IF NOT EXISTS idx_likes_user
    ON likes (user_id);

-- Индекс по другу для быстрых обратных запросов дружбы
CREATE INDEX IF NOT EXISTS idx_friendships_friend
    ON friendships (friend_id);

-- Индексы по отзывам для выборки по фильму и пользователю
CREATE INDEX IF NOT EXISTS idx_reviews_film
    ON reviews (film_id);

CREATE INDEX IF NOT EXISTS idx_reviews_user
    ON reviews (user_id);

-- Индекс по пользователю, оценивающему отзыв, для выборки оценок по автору
CREATE INDEX IF NOT EXISTS idx_review_rating_user
    ON review_rating (user_id);

-- Композитный индекс для ленты активности пользователя по времени
CREATE INDEX IF NOT EXISTS idx_events_user_ts
    ON events (user_id, event_timestamp);
