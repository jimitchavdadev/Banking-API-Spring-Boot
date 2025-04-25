# Banking API

## License
MIT License

## Overview
A secure and scalable RESTful banking API built with Spring Boot, providing account summary and transaction management functionalities. Supports operations like deposits, withdrawals, transfers, and account queries, backed by a MySQL database with automated tests for reliability.

## Table of Contents
- [Project Overview](#project-overview)
- [Features](#features)
- [Technologies](#technologies)
- [Database Structure](#database-structure)
- [API Endpoints](#api-endpoints)
- [Setup Instructions](#setup-instructions)
- [Running Tests](#running-tests)
- [Contributing](#contributing)
- [License](#license)

## Project Overview
The Banking API manages banking operations, enabling users to retrieve account summaries and perform transactions. Built with Spring Boot and MySQL, and includes test scripts to ensure functionality.

## Features
- Retrieve account summaries by account or customer ID.
- Perform transactions: deposits, withdrawals, and transfers.
- Query transactions by ID, account, or date range.
- Input validation and error handling.
- Automated bash test scripts.
- MySQL views and stored procedures for efficiency.

## Technologies
- Java 17
- Spring Boot 3.3.4
- MySQL 8.3.0
- Maven
- Bash
- Lombok
- HikariCP
- Hibernate

## Database Structure
### Tables
- **accounts**
```sql
CREATE TABLE accounts (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(50) NOT NULL UNIQUE,
    account_type ENUM('SAVINGS', 'CHECKING') NOT NULL,
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    customer_id INT NOT NULL,
    branch_id INT NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id)
);
```
- **customers**
```sql
CREATE TABLE customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20)
);
```
- **branches**
```sql
CREATE TABLE branches (
    branch_id INT AUTO_INCREMENT PRIMARY KEY,
    branch_name VARCHAR(100) NOT NULL,
    branch_address VARCHAR(255)
);
```
- **transactions**
```sql
CREATE TABLE transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    transaction_type ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER') NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    transaction_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(255),
    target_account_id INT,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id),
    FOREIGN KEY (target_account_id) REFERENCES accounts(account_id)
);
```

### View: `account_summary`
```sql
CREATE VIEW account_summary AS
SELECT
    a.account_id,
    a.account_number,
    a.account_type,
    a.balance,
    a.customer_id,
    c.first_name,
    c.last_name,
    b.branch_name
FROM
    accounts a
    INNER JOIN customers c ON a.customer_id = c.customer_id
    INNER JOIN branches b ON a.branch_id = b.branch_id;
```

### Stored Procedure: `transfer_money`
```sql
DELIMITER //
CREATE PROCEDURE transfer_money(
    IN from_account_id INT,
    IN to_account_id INT,
    IN transfer_amount DECIMAL(15,2),
    IN description VARCHAR(255)
)
BEGIN
    DECLARE from_balance DECIMAL(15,2);
    START TRANSACTION;
    SELECT balance INTO from_balance
    FROM accounts
    WHERE account_id = from_account_id
    FOR UPDATE;

    IF from_balance < transfer_amount THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Insufficient balance for transfer';
    END IF;

    UPDATE accounts SET balance = balance - transfer_amount WHERE account_id = from_account_id;
    UPDATE accounts SET balance = balance + transfer_amount WHERE account_id = to_account_id;

    INSERT INTO transactions (account_id, transaction_type, amount, transaction_date, description, target_account_id)
    VALUES (from_account_id, 'TRANSFER', transfer_amount, NOW(), description, to_account_id);

    INSERT INTO transactions (account_id, transaction_type, amount, transaction_date, description, target_account_id)
    VALUES (to_account_id, 'TRANSFER', transfer_amount, NOW(), CONCAT('Received from account ', from_account_id), from_account_id);
    
    COMMIT;
END //
DELIMITER ;
```

## API Endpoints
Base URL: `http://localhost:8080`

## 1. Branches
The branches table stores information about bank branches. Endpoints allow managing branch data.

### Endpoints

| Method | Endpoint | Description | Request Body Example | Response |
|--------|----------|-------------|-----------------------|----------|
| GET | /branches | Retrieve a list of all branches | N/A | List of branches (branch_id, branch_name, branch_address, branch_phone, created_at) |
| GET | /branches/{branch_id} | Retrieve details of a specific branch by ID | N/A | Single branch object |
| POST | /branches | Create a new branch | { "branch_name": "New Branch", "branch_address": "789 Elm St", "branch_phone": "555-0103" } | Created branch object |
| PUT | /branches/{branch_id} | Update an existing branch | { "branch_name": "Updated Branch", "branch_address": "789 Elm St", "branch_phone": "555-0104" } | Updated branch object |
| DELETE | /branches/{branch_id} | Delete a branch (if not linked to accounts) | N/A | Success message |

---

## 2. Customers
The customers table stores customer information. Endpoints manage customer profiles.

### Endpoints

| Method | Endpoint | Description | Request Body Example | Response |
|--------|----------|-------------|-----------------------|----------|
| GET | /customers | Retrieve a list of all customers | N/A | List of customers (customer_id, first_name, last_name, email, phone, address, date_of_birth, created_at) |
| GET | /customers/{customer_id} | Retrieve details of a specific customer by ID | N/A | Single customer object |
| GET | /customers/email/{email} | Retrieve customer by email (using index) | N/A | Single customer object |
| POST | /customers | Create a new customer | { "first_name": "Alice", "last_name": "Brown", "email": "alice.brown@email.com", "phone": "555-9012", "address": "456 Birch St", "date_of_birth": "1988-03-10" } | Created customer object |
| PUT | /customers/{customer_id} | Update an existing customer | { "first_name": "Alice", "last_name": "Brown", "email": "alice.brown@email.com", "phone": "555-9013", "address": "456 Birch St", "date_of_birth": "1988-03-10" } | Updated customer object |
| DELETE | /customers/{customer_id} | Delete a customer (if not linked to accounts) | N/A | Success message |

---

## 3. Accounts
The accounts table stores account details. Endpoints manage account creation, updates, and queries.

### Endpoints

| Method | Endpoint | Description | Request Body Example | Response |
|--------|----------|-------------|-----------------------|----------|
| GET | /accounts | Retrieve a list of all accounts | N/A | List of accounts (account_id, customer_id, branch_id, account_type, account_number, balance, opened_at, status) |
| GET | /accounts/{account_id} | Retrieve details of a specific account by ID | N/A | Single account object |
| GET | /accounts/customer/{customer_id} | Retrieve all accounts for a specific customer | N/A | List of accounts for the customer |
| GET | /accounts/number/{account_number} | Retrieve account by account number (using index) | N/A | Single account object |
| POST | /accounts | Create a new account | { "customer_id": 1, "branch_id": 1, "account_type": "SAVINGS", "account_number": "SAV901234", "balance": 1000.00, "status": "ACTIVE" } | Created account object |
| PUT | /accounts/{account_id} | Update an existing account (e.g., status or balance) | { "account_type": "CHECKING", "status": "INACTIVE" } | Updated account object |
| DELETE | /accounts/{account_id} | Delete an account (if balance is zero) | N/A | Success message |

---

## 4. Transactions
The transactions table records all account transactions. Endpoints handle transaction history and operations.

### Endpoints

| Method | Endpoint | Description | Request Body Example | Response |
|--------|----------|-------------|-----------------------|----------|
| GET | /transactions | Retrieve a list of all transactions | N/A | List of transactions (transaction_id, account_id, transaction_type, amount, transaction_date, description, target_account_id) |
| GET | /transactions/{transaction_id} | Retrieve details of a specific transaction by ID | N/A | Single transaction object |
| GET | /transactions/account/{account_id} | Retrieve all transactions for a specific account | N/A | List of transactions for the account |
| GET | /transactions/date | Retrieve transactions within a date range (using index) | Query params: start_date, end_date | List of transactions in the date range |
| POST | /transactions/deposit | Record a deposit transaction | { "account_id": 1, "amount": 1000.00, "description": "Cash deposit" } | Created transaction object |
| POST | /transactions/withdrawal | Record a withdrawal transaction | { "account_id": 1, "amount": 500.00, "description": "ATM withdrawal" } | Created transaction object |
| POST | /transactions/transfer | Perform a money transfer (calls transfer_money procedure) | { "from_account_id": 1, "to_account_id": 2, "amount": 500.00, "description": "Transfer to friend" } | Success message or transaction details |

---

## 5. Account Summary (View)
The account_summary view provides a joined view of accounts, customers, and branches. Endpoints expose this summary.

### Endpoints

| Method | Endpoint | Description | Request Body Example | Response |
|--------|----------|-------------|-----------------------|----------|
| GET | /account-summary | Retrieve account summaries for all accounts | N/A | List of summaries (account_id, account_number, account_type, balance, first_name, last_name, branch_name) |
| GET | /account-summary/{account_id} | Retrieve summary for a specific account | N/A | Single summary object |
| GET | /account-summary/customer/{customer_id} | Retrieve summaries for all accounts of a customer | N/A | List of summaries for the customer |


### Error Responses
- 404: `{"error":"Not Found","message":"Account not found with id: 999"}`
- 400: `{"error":"Bad Request","message":"Insufficient funds for withdrawal"}`

## Setup Instructions
### Prerequisites
- Java 17
- Maven 3.8+
- MySQL 8.3.0
- Bash

### Steps
```bash
git clone https://github.com/yourusername/banking-api.git
cd banking-api
```
Configure MySQL:
```sql
CREATE DATABASE banking_db;
```
Set up credentials in `~/.my.cnf`:
```ini
[client]
user=<username>
password=<your password>
database=banking_db
```
```bash
chmod 600 ~/.my.cnf
mysql -u testing -p banking_db < src/main/resources/schema.sql
```
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/banking_db
spring.datasource.username=<username>
spring.datasource.password=<database password>
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
```
Build and run:
```bash
mvn clean package
mvn spring-boot:run
```

## Contributing
1. Fork the repository.
2. Create a feature branch.
3. Commit your changes.
4. Push to your fork.
5. Open a pull request.

## License
This project is licensed under the MIT License.

---

### Notes
- Replace repository URL with your actual GitHub URL.
- Ensure `schema.sql` includes all schema, views, procedures, and sample data.
- Security: Consider Spring Security for production.
- CI/CD: Add GitHub Actions for automation.

