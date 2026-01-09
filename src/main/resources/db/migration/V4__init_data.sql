-- noinspection SqlResolve
MERGE INTO mpa (mpa_id, mpa_name) KEY (mpa_id) VALUES
    (1, 'G'),
    (2, 'PG'),
    (3, 'PG-13'),
    (4, 'R'),
    (5, 'NC-17');


MERGE INTO genres (genre_id, genre_name) KEY (genre_id) VALUES
    (1, 'Комедия'),
    (2, 'Драма'),
    (3, 'Мультфильм'),
    (4, 'Триллер'),
    (5, 'Документальный'),
    (6, 'Боевик');


INSERT INTO users (email, login, user_name, birthday) VALUES
('ivanov@mail.ru', 'ivanov', 'Иван Иванов', '1990-05-15'),
('petrova@mail.ru', 'petrova', 'Мария Петрова', '1995-08-22'),
('sidorov@mail.ru', 'sidorov', 'Алексей Сидоров', '1988-12-03');


INSERT INTO films (film_name, description, release_date, duration, mpa_id) VALUES
('Матрица', 'Научно-фантастический боевик о хакере Нео', '1999-03-31', 136, 4),
('Король Лев', 'Мультфильм о приключениях львенка Симбы', '1994-06-24', 88, 1),
('Побег из Шоушенка', 'Драма о несправедливо осужденном банкире', '1994-09-23', 142, 4);


