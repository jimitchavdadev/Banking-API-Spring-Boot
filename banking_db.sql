-- Create the banking database
CREATE DATABASE IF NOT EXISTS banking_db;
USE banking_db;

-- All the Tables
-- Create Branches table
CREATE TABLE branches (
                          branch_id INT PRIMARY KEY AUTO_INCREMENT,
                          branch_name VARCHAR(100) NOT NULL,
                          branch_address VARCHAR(255) NOT NULL,
                          branch_phone VARCHAR(15),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Customers table
CREATE TABLE customers (
                           customer_id INT PRIMARY KEY AUTO_INCREMENT,
                           first_name VARCHAR(50) NOT NULL,
                           last_name VARCHAR(50) NOT NULL,
                           email VARCHAR(100) UNIQUE NOT NULL,
                           phone VARCHAR(15),
                           address VARCHAR(255),
                           date_of_birth DATE,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Accounts table
CREATE TABLE accounts (
                          account_id INT PRIMARY KEY AUTO_INCREMENT,
                          customer_id INT NOT NULL,
                          branch_id INT NOT NULL,
                          account_type ENUM('SAVINGS', 'CHECKING', 'LOAN') NOT NULL,
                          account_number VARCHAR(20) UNIQUE NOT NULL,
                          balance DECIMAL(15, 2) DEFAULT 0.00,
                          opened_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          status ENUM('ACTIVE', 'INACTIVE', 'CLOSED') DEFAULT 'ACTIVE',
                          FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
                          FOREIGN KEY (branch_id) REFERENCES branches(branch_id) ON DELETE RESTRICT
);

-- Create Transactions table
CREATE TABLE transactions (
                              transaction_id INT PRIMARY KEY AUTO_INCREMENT,
                              account_id INT NOT NULL,
                              transaction_type ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER', 'PAYMENT') NOT NULL,
                              amount DECIMAL(15, 2) NOT NULL,
                              transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              description VARCHAR(255),
                              target_account_id INT,
                              FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE,
                              FOREIGN KEY (target_account_id) REFERENCES accounts(account_id) ON DELETE SET NULL
);

-- Query Optimization
-- Create indexes for better query performance
CREATE INDEX idx_customer_email ON customers(email);
CREATE INDEX idx_account_number ON accounts(account_number);
CREATE INDEX idx_transaction_date ON transactions(transaction_date);

-- Create a view for account summary
CREATE VIEW account_summary AS
SELECT
    a.account_id,
    a.account_number,
    a.account_type,
    a.balance,
    c.first_name,
    c.last_name,
    b.branch_name
FROM accounts a
         JOIN customers c ON a.customer_id = c.customer_id
         JOIN branches b ON a.branch_id = b.branch_id;

-- Create a stored procedure to transfer money
DELIMITER //
CREATE PROCEDURE transfer_money(
    IN from_account_id INT,
    IN to_account_id INT,
    IN transfer_amount DECIMAL(15, 2),
    IN description VARCHAR(255)
)
BEGIN
    DECLARE from_balance DECIMAL(15, 2);

    -- Start transaction
    START TRANSACTION;

    -- Check if from_account has sufficient balance
    SELECT balance INTO from_balance
    FROM accounts
    WHERE account_id = from_account_id
        FOR UPDATE;

    IF from_balance >= transfer_amount THEN
        -- Update from_account balance
        UPDATE accounts
        SET balance = balance - transfer_amount
        WHERE account_id = from_account_id;

        -- Update to_account balance
        UPDATE accounts
        SET balance = balance + transfer_amount
        WHERE account_id = to_account_id;

        -- Record withdrawal transaction
        INSERT INTO transactions (account_id, transaction_type, amount, description, target_account_id)
        VALUES (from_account_id, 'TRANSFER', transfer_amount, description, to_account_id);

        -- Record deposit transaction
        INSERT INTO transactions (account_id, transaction_type, amount, description, target_account_id)
        VALUES (to_account_id, 'TRANSFER', transfer_amount, description, from_account_id);

        COMMIT;
    ELSE
        ROLLBACK;
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Insufficient balance for transfer';
    END IF;
END //
DELIMITER ;

-- Create a trigger to prevent negative balances
DELIMITER //
CREATE TRIGGER prevent_negative_balance
    BEFORE UPDATE ON accounts
    FOR EACH ROW
BEGIN
    IF NEW.balance < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Account balance cannot be negative';
    END IF;
END //
DELIMITER ;

-- Verify table creation
SHOW TABLES;

-- Verify sample data
SELECT * FROM branches LIMIT 5;
SELECT * FROM customers LIMIT 5;
SELECT * FROM accounts LIMIT 5;
SELECT * FROM transactions LIMIT 5;
SELECT * FROM account_summary LIMIT 5;