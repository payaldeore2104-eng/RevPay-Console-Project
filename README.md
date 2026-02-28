# RevPay - Console Banking Application

RevPay is a feature-rich console-based banking application written in Java. It allows users to manage their finances, send money, pay bills, and manage business invoices securely.

## Features

### User & Security
- **Secure Login & Registration**: Passwords hashed with BCrypt. Supports Username/Email/Phone.
- **Account Locking**: Accounts lock automatically after 5 failed login attempts.
- **Transaction PIN**: Required for sending and withdrawing money.
- **Forgot Password**: Secure recovery using Security Question.
- **Encryption**: Card details stored using AES-256 encryption.

### Wallet & Transactions
- **Add/Withdraw Money**: Manage wallet balance with low-balance alerts (< $500).
- **Send Money**: Transfer funds using Email, Phone, or Username.
- **Request Money**: Request payments and track status (Pending/Accepted/Declined).
- **Transaction History**: View logs with filters (Date, Amount, Type).

### Business Features
- **Business Accounts**: Register with business details (Tax ID, Address).
- **Invoicing**: Create and track invoices.
- **Analytics**:
  - Total Revenue reporting.
  - Monthly Transaction breakdown.
  - Top Customers list.
  - Pending Invoices tracking.

### Notifications
- **Real-time Alerts**: For sent/received money, requests, and low balance.

## Tech Stack
- **Language**: Java 8+
- **Database**: MySQL (JDBC)
- **Security**: BCrypt, AES-256
- **Logging**: Log4j2
- **Testing**: JUnit 5
- **Build Tool**: Maven

## Setup Instructions

### 1. Database Setup
1. Install MySQL Server.
2. Create a database named `revpay_db`.
3. Run the script `db_schema.sql` located in the project root to create tables.
4. Update `src/main/java/com/revpay/repository/DBConnection.java` with your MySQL username and password.

### 2. Build & Run
1. Open terminal in the project root.
2. Build the project using Maven:
   ```sh
   mvn clean install
   ```
3. Run the application:
   ```sh
   mvn exec:java
   ```

## Usage
1. **Register**: Create a new account (Personal or Business).
2. **Login**: Use your email/phone and password.
3. **Wallet**: Add some money to start.
4. **Interact**: Send money to other registered users or create invoices.

## Future Enhancements
- Web Interface / REST API.
- Scheduled recurring payments.
- Admin dashboard for system management.
