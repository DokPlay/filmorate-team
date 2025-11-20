-- noinspection SqlResolve
-- Добавление таблицы отзывов
CREATE TABLE IF NOT EXISTS reviews (
    review_id INTEGER AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    is_positive BOOLEAN NOT NULL,
    user_id INTEGER NOT NULL,
    film_id INTEGER NOT NULL,
    useful INTEGER DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE
);

-- Индексы для таблицы отзывов
CREATE INDEX IF NOT EXISTS idx_reviews_film_id ON reviews(film_id);
CREATE INDEX IF NOT EXISTS idx_reviews_user_id ON reviews(user_id);
CREATE INDEX IF NOT EXISTS idx_reviews_useful ON reviews(useful);

-- Один пользователь - один отзыв на фильм, отключено для простоты тестирования, позже убрать.
--ALTER TABLE reviews ADD CONSTRAINT IF NOT EXISTS unique_user_film_review UNIQUE (user_id, film_id);

CREATE TABLE review_ratings (
    review_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    is_positive BOOLEAN NOT NULL, -- true = лайк, false = дизлайк
    PRIMARY KEY (review_id, user_id),
    FOREIGN KEY (review_id) REFERENCES reviews(review_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
