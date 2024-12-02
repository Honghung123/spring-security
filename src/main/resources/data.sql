-- Insert your data here

INSERT INTO users (user_id, name, username, email, password)
VALUES (gen_random_uuid(), 'Super Admin', 'admin', 'admin@gmail.com',
        '$2a$10$XgK51b74JX5pEhGfOdCaNuDHUzbnfvu1DSfoMmVHwa758sfgo4Y1a'),
       (gen_random_uuid(), 'Hong Hung', 'user', 'user@gmail.com',
        '$2a$10$XgK51b74JX5pEhGfOdCaNuDHUzbnfvu1DSfoMmVHwa758sfgo4Y1a'),
       (gen_random_uuid(), 'Vuong Huy Khai', 'manager', 'manager@gmail.com',
        '$2a$10$XgK51b74JX5pEhGfOdCaNuDHUzbnfvu1DSfoMmVHwa758sfgo4Y1a');

INSERT INTO roles (name, description)
VALUES ('ADMIN', 'Administrator'),
       ('USER', 'User'),
       ('MANAGER', 'Manager');

INSERT INTO user_has_roles (user_id, role_id)
VALUES ((SELECT user_id FROM users WHERE username = 'admin'), (SELECT role_id FROM roles WHERE name = 'ADMIN')),
       ((SELECT user_id FROM users WHERE username = 'user'), (SELECT role_id FROM roles WHERE name = 'USER')),
       ((SELECT user_id FROM users WHERE username = 'manager'), (SELECT role_id FROM roles WHERE name = 'MANAGER'));