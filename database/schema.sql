CREATE DATABASE bank_system;
USE bank_system;

CREATE TABLE accounts (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    balance DOUBLE DEFAULT 0
);
