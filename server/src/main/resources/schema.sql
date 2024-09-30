-- Очистка всех таблиц
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS item_requests;
DROP TABLE IF EXISTS users;


CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS item_requests (
    id SERIAL PRIMARY KEY,
    description TEXT NOT NULL,
    user_id INTEGER NOT NULL,  -- Изменено: поле для хранения пользователя, создавшего запрос
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS items (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_available BOOLEAN NOT NULL,
    owner_id INTEGER NOT NULL,
    request_id INTEGER,
    FOREIGN KEY (owner_id) REFERENCES users(id),
    FOREIGN KEY (request_id) REFERENCES item_requests(id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id SERIAL PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id INTEGER NOT NULL,
    booker_id INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (item_id) REFERENCES items(id),
    FOREIGN KEY (booker_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS comments (
    id SERIAL PRIMARY KEY,
    text TEXT NOT NULL,
    item_id INTEGER NOT NULL,
    author_id INTEGER NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    FOREIGN KEY (item_id) REFERENCES items(id),
    FOREIGN KEY (author_id) REFERENCES users(id)
);
