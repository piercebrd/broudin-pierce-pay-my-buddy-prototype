DELETE FROM transactions;
DELETE FROM users;

INSERT INTO users (id, email, username, password, balance, created_at)
VALUES (1, 'alice@example.com', 'Alice', '$2a$10$encoded', 100.00, NOW());

INSERT INTO users (id, email, username, password, balance, created_at)
VALUES (2, 'bob@example.com', 'Bob', '$2a$10$encoded', 50.00, NOW());

INSERT INTO transactions (id, sender_id, receiver_id, description, amount, created_at)
VALUES (1, 1, 2, 'Lunch', 20.00, NOW());
