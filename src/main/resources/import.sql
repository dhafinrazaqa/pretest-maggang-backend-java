-- import.sql

-- User (single statement, explicit semicolon)
INSERT INTO users (id, name, email, created_at) VALUES (1, 'Alice Smith', 'alice@example.com', CURRENT_TIMESTAMP);

-- Products (each as a completely separate statement, ending with a semicolon)
INSERT INTO products (id, name, description, price, stock) VALUES (101, 'Wireless Mouse', 'Ergonomic wireless mouse with DPI settings.', 25.99, 100);
INSERT INTO products (id, name, description, price, stock) VALUES (102, 'Mechanical Keyboard', 'RGB mechanical keyboard with blue switches.', 89.99, 50);
INSERT INTO products (id, name, description, price, stock) VALUES (103, 'HD Webcam', '1080p webcam with built-in microphone.', 45.00, 75);