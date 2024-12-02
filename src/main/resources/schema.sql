-- Define your table structures here
-- DELETE ALL TABLES
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

CREATE TABLE users
(
    user_id  UUID PRIMARY KEY,
    name     VARCHAR(50)   NOT NULL,
    username VARCHAR(25)   NOT NULL UNIQUE,
    email    VARCHAR(50)   NOT NULL UNIQUE,
    password VARCHAR(1024) NOT NULL,
    status   BOOLEAN DEFAULT TRUE
);


CREATE TABLE roles
(
    role_id     SERIAL PRIMARY KEY,
    name        VARCHAR(50) NOT NULL,
    description TEXT
);
CREATE TABLE user_has_roles
(
    id      SERIAL PRIMARY KEY,
    user_id UUID    NOT NULL,
    role_id INTEGER NOT NULL,
    CONSTRAINT fk_role_user_roles
        FOREIGN KEY (role_id) REFERENCES roles (role_id),
    CONSTRAINT fk_user_user_roles
        FOREIGN KEY (user_id) REFERENCES users (user_id)
);