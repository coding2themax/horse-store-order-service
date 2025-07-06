
-- Create the database schema for Horse Store
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create schema for horse order
CREATE SCHEMA IF NOT EXISTS horse_order;
COMMENT ON SCHEMA horse_order IS 'Schema for horse order related tables and functionality';
-- Users table
CREATE TABLE IF NOT EXISTS horse_order.users (
    user_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    address TEXT,
    phone VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    preferences JSONB DEFAULT '{}'::jsonb
);

-- Orders table
CREATE TABLE IF NOT EXISTS horse_order.orders (
    order_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES horse_order.users(user_id),
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    shipping_info JSONB NOT NULL,
    payment_info JSONB NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_users_email ON horse_order.users(email);
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON horse_order.orders(user_id);

-- Insert test users
INSERT INTO horse_order.users (user_id, username, email, password_hash, first_name, last_name, address, phone)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'testuser1', 'test1@example.com', 'hash1', 'John', 'Doe', '123 Test St', '555-0001'),
    ('22222222-2222-2222-2222-222222222222', 'testuser2', 'test2@example.com', 'hash2', 'Jane', 'Smith', '456 Test Ave', '555-0002');
-- Insert test orders
INSERT INTO horse_order.orders (order_id, user_id, order_date, status, shipping_info, payment_info, total_amount)
VALUES
    ('33333333-3333-3333-3333-333333333333', '11111111-1111-1111-1111-111111111111',
     '2023-10-01 10:00:00', 'pending',
     '{"address": "123 Test St", "city": "Test City", "zip": "12345"}',
     '{"method": "credit_card", "card_number": "4111111111111111", "expiry": "12/25"}',
     100.00),
    ('44444444-4444-4444-4444-444444444444', '22222222-2222-2222-2222-222222222222',
     '2023-10-02 11:00:00', 'shipped',
     '{"address": "456 Test Ave", "city": "Test City", "zip": "12345"}',
     '{"method": "paypal", "email": "test2@example.com"}',
     200.00);