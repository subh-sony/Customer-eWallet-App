-- Database setup script for E-Wallet Microservices

-- Create databases
CREATE DATABASE ewallet_customer;
CREATE DATABASE ewallet_transaction;

-- Connect to customer database
\c ewallet_customer;

-- Customer table will be created automatically by JPA
-- But you can create it manually if needed:
/*
CREATE TABLE customer (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255),
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    status VARCHAR(50) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    mpin VARCHAR(10)
);

CREATE TABLE customer_account (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    account_number VARCHAR(50) UNIQUE NOT NULL,
    balance DECIMAL(19,2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);

CREATE TABLE otp (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    otp VARCHAR(10) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP,
    expires_at TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);
*/

-- Connect to transaction database
\c ewallet_transaction;

-- Transaction table will be created automatically by JPA
-- But you can create it manually if needed:
/*
CREATE TABLE transaction (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    from_account_number VARCHAR(50) NOT NULL,
    to_account_number VARCHAR(50) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    updated_date TIMESTAMP
);
*/

