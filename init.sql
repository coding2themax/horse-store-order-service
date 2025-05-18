-- Create the database schema for Horse Store
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create schema for horse order
CREATE SCHEMA IF NOT EXISTS horse_order;

COMMENT ON SCHEMA horse_order IS 'Schema for horse order related tables and functionality';

-- Users table
CREATE TABLE horse_order.users (
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

-- Categories table
CREATE TABLE horse_order.categories (
    category_id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    parent_id INT REFERENCES categories(category_id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Horses table (for the actual horses)
CREATE TABLE horse_order.horses (
    horse_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    breed VARCHAR(50) NOT NULL,
    age NUMERIC(3,1) NOT NULL,
    color VARCHAR(50),
    gender VARCHAR(10) CHECK (gender IN ('Male', 'Female')),
    price DECIMAL(10,2) NOT NULL,
    details JSONB NOT NULL DEFAULT '{}'::jsonb, -- For flexible attributes like lineage, training, etc.
    health_records JSONB DEFAULT '{}'::jsonb,
    is_sold BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Products table (for horse-related items like saddles, feed, etc.)
CREATE TABLE horse_order.products (
    product_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    category_id INT REFERENCES categories(category_id),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    specifications JSONB DEFAULT '{}'::jsonb, -- For detailed product specs
    stock_quantity INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Orders table
CREATE TABLE horse_order.orders (
    order_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(user_id),
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    shipping_info JSONB NOT NULL,
    payment_info JSONB NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0
);

-- Order Items table
CREATE TABLE horse_order.order_items (
    order_item_id SERIAL PRIMARY KEY,
    order_id UUID REFERENCES orders(order_id),
    product_id UUID REFERENCES products(product_id),
    horse_id UUID REFERENCES horses(horse_id),
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10,2) NOT NULL,
    CHECK (product_id IS NOT NULL OR horse_id IS NOT NULL),
    CHECK (NOT (product_id IS NOT NULL AND horse_id IS NOT NULL))
);

-- Create appropriate indices
CREATE INDEX idx_users_email ON horse_order.users(email);
CREATE INDEX idx_products_category ON horse_order.products(category_id);
CREATE INDEX idx_horses_breed ON horse_order.horses(breed);
CREATE INDEX idx_horses_details ON horse_order.horses USING GIN (details);
CREATE INDEX idx_products_specs ON horse_order.products USING GIN (specifications);
CREATE INDEX idx_orders_user ON horse_order.orders(user_id);
CREATE INDEX idx_order_items_order ON horse_order.order_items(order_id);
CREATE INDEX idx_order_items_product ON horse_order.order_items(product_id) WHERE product_id IS NOT NULL;
CREATE INDEX idx_order_items_horse ON horse_order.order_items(horse_id) WHERE horse_id IS NOT NULL;

-- Stored Procedures

-- Check product availability
CREATE OR REPLACE FUNCTION horse_order.check_product_availability(p_product_id UUID)
RETURNS INT AS $$
DECLARE
    available INT;
BEGIN
    SELECT stock_quantity INTO available FROM horse_order.products WHERE product_id = p_product_id;
    RETURN COALESCE(available, 0);
END;
$$ LANGUAGE plpgsql;

-- Create new order
CREATE OR REPLACE PROCEDURE horse_order.create_order(
    p_user_id UUID,
    p_shipping_info JSONB,
    p_payment_info JSONB,
    OUT p_order_id UUID
)
AS $$
BEGIN
    INSERT INTO horse_order.orders (user_id, shipping_info, payment_info)
    VALUES (p_user_id, p_shipping_info, p_payment_info)
    RETURNING order_id INTO p_order_id;
END;
$$ LANGUAGE plpgsql;

-- Add product to order
CREATE OR REPLACE PROCEDURE horse_order.add_product_to_order(
    p_order_id UUID,
    p_product_id UUID,
    p_quantity INT
)
AS $$
DECLARE
    v_price DECIMAL(10,2);
    v_available INT;
BEGIN
    -- Check availability
    SELECT price, stock_quantity INTO v_price, v_available 
    FROM horse_order.products WHERE product_id = p_product_id;
    
    IF v_available < p_quantity THEN
        RAISE EXCEPTION 'Not enough stock. Available: %, Requested: %', v_available, p_quantity;
    END IF;
    
    -- Add to order
    INSERT INTO horse_order.order_items (order_id, product_id, quantity, unit_price)
    VALUES (p_order_id, p_product_id, p_quantity, v_price);
    
    -- Update order total
    UPDATE horse_order.orders SET total_amount = total_amount + (v_price * p_quantity)
    WHERE order_id = p_order_id;
    
    -- Update inventory
    UPDATE horse_order.products SET 
        stock_quantity = stock_quantity - p_quantity,
        updated_at = CURRENT_TIMESTAMP
    WHERE product_id = p_product_id;
END;
$$ LANGUAGE plpgsql;

-- Add horse to order
CREATE OR REPLACE PROCEDURE horse_order.add_horse_to_order(
    p_order_id UUID,
    p_horse_id UUID
)
AS $$
DECLARE
    v_price DECIMAL(10,2);
    v_is_sold BOOLEAN;
BEGIN
    -- Check if horse is available
    SELECT price, is_sold INTO v_price, v_is_sold 
    FROM horse_order.horses WHERE horse_id = p_horse_id;
    
    IF v_is_sold THEN
        RAISE EXCEPTION 'Horse is already sold';
    END IF;
    
    -- Add to order
    INSERT INTO horse_order.order_items (order_id, horse_id, quantity, unit_price)
    VALUES (p_order_id, p_horse_id, 1, v_price);
    
    -- Update order total
    UPDATE horse_order.orders SET total_amount = total_amount + v_price
    WHERE order_id = p_order_id;
    
    -- Mark horse as sold
    UPDATE horse_order.horses SET 
        is_sold = TRUE,
        updated_at = CURRENT_TIMESTAMP
    WHERE horse_id = p_horse_id;
END;
$$ LANGUAGE plpgsql;

-- Complete order procedure
CREATE OR REPLACE PROCEDURE horse_order.complete_order(p_order_id UUID)
AS $$
BEGIN
    UPDATE horse_order.orders SET status = 'completed' WHERE order_id = p_order_id;
END;
$$ LANGUAGE plpgsql;

