INSERT INTO USERS (username, password, role, email, phone)
VALUES ('cust1', '$2a$10$7QJ0JvYc9gYkzWmZ5i5k9evwzQ0Z1gqQ9Z0Z5I1BqzJYzq1K9cQ0e', 'ROLE_CUSTOMER', 'cust1@example.com', '9999999999');

INSERT INTO WALLET (user_id, balance, currency)
VALUES (1, 1000.0, 'INR');
