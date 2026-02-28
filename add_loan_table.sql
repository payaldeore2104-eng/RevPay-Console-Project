-- Add loan_applications table to existing RevPay database
USE revpay_db;

CREATE TABLE IF NOT EXISTS loan_applications (
    loan_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    purpose VARCHAR(500),
    interest_rate DECIMAL(5, 2) DEFAULT 8.5,
    duration_months INT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'ACTIVE', 'CLOSED')),
    applied_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approved_date TIMESTAMP NULL,
    amount_paid DECIMAL(12, 2) DEFAULT 0.00,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_loan_user ON loan_applications(user_id);

-- Verify table was created
SELECT 'Loan table created successfully!' AS Status;
