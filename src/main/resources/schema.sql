CREATE TABLE IF NOT EXISTS mpa_rating
    (id BIGINT PRIMARY KEY,
    name VARCHAR(50) NOT NULL);

CREATE TABLE IF NOT EXISTS genres
    (id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL);

CREATE TABLE IF NOT EXISTS users
    (id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    login VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    birthday DATE NOT NULL,
    CONSTRAINT valid_email CHECK (email LIKE '%@%.%'),
    CONSTRAINT future_birthday CHECK (birthday <= CURRENT_DATE));

CREATE TABLE IF NOT EXISTS user_friends
    (user_id BIGINT,
    friend_id BIGINT,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE);

CREATE TABLE IF NOT EXISTS films
   (id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    release_date DATE NOT NULL,
    duration BIGINT NOT NULL,
    rating_id VARCHAR(10),
    CONSTRAINT positive_duration CHECK (duration > 0),
    CONSTRAINT valid_release_date CHECK (release_date >= '1895-12-28'),
    FOREIGN KEY (rating_id) REFERENCES mpa_rating(id));

CREATE TABLE IF NOT EXISTS film_genres
    (film_id BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE);

CREATE TABLE IF NOT EXISTS likes
    (film_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE);