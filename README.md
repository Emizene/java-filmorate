# java-filmorate
Template repository for Filmorate project.

Некоторые примеры запросов CRUD операций для таблицы films.

Create:
INSERT INTO films (film_id, name, description, release_date, duration, rating_id)
VALUES (1, 'Один дома', 'Американское семейство отправляется из Чикаго в Европу,
но в спешке сборов бестолковые родители забывают дома... одного из своих детей', '2014-11-06', 100, 'PG');

Read:
SELECT f.*, r.name as rating_name
FROM films f
JOIN mpa_rating r ON f.rating_id = r.rating_id
WHERE f.rating_id = 'PG';

Update:
UPDATE films
SET duration = duration + 3
WHERE film_id = 1;

Delete:
DELETE FROM films
WHERE film_id = 1;


Некоторые примеры запросов CRUD операций для таблицы users.

Create:
INSERT INTO users (user_id, name, login, email, birthday)
VALUES (1, 'Иван Иванов, 'ivan_01', 'ivan@y.ru', '1999-09-19');

Read:
SELECT * FROM users
WHERE name LIKE 'Иван';

Update:
UPDATE users
SET email = 'ivan01@y.ru'
WHERE user_id = 1;

Delete:
DELETE FROM users
WHERE user_id = 1;
