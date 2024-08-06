MERGE INTO GENRE (NAME)
KEY (NAME)
VALUES
('Комедия'),
('Драма'),
('Мультфильм'),
('Триллер'),
('Документальный'),
('Боевик');

MERGE INTO rating (name)
KEY (name)
VALUES
('G'),
('PG'),
('PG-13'),
('R'),
('NC-17');

