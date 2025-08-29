-- =========================================================
-- DB Initialization Script (Fixed to match UUID entities)
-- =========================================================

-- Enable UUID extension for Postgres
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =========================================================
-- Lookup Tables for enums
-- =========================================================

-- Roles lookup
CREATE TABLE IF NOT EXISTS roles (
        name VARCHAR(20) PRIMARY KEY
    );

INSERT INTO roles (name) VALUES ('ADMIN'), ('USER')
    ON CONFLICT DO NOTHING;

-- Order statuses lookup
CREATE TABLE IF NOT EXISTS order_statuses (
        name VARCHAR(20) PRIMARY KEY
    );

INSERT INTO order_statuses (name) VALUES ('DONE'), ('IN_PROGRESS'), ('PENDING')
    ON CONFLICT DO NOTHING;

-- =========================================================
-- Main Tables
-- =========================================================

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    first_name VARCHAR(30) NOT NULL,
    last_name VARCHAR(30) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL REFERENCES roles(name)
    );

-- Products table
CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(400),
    actual_price NUMERIC(10,2) NOT NULL,
    selling_price NUMERIC(10,2) NOT NULL,
    available_quantity INTEGER NOT NULL
    );

-- Orders table
CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    order_date TIMESTAMP NOT NULL,
    total_cost NUMERIC(38,2) NOT NULL,
    status VARCHAR(20) NOT NULL REFERENCES order_statuses(name)
    );

-- Junction table: orders <-> products
CREATE TABLE IF NOT EXISTS order_products (
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id),
    product_quantity INTEGER NOT NULL,
    PRIMARY KEY (order_id, product_id)
    );
