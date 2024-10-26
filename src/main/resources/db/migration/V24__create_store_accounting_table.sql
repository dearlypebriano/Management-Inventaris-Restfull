CREATE TABLE IF NOT EXISTS store_accounting (
    id VARCHAR(36) NOT NULL UNIQUE PRIMARY KEY,
    store_id VARCHAR(36) NOT NULL,
    income_date VARCHAR(255) NOT NULL,
    daily_income DOUBLE NOT NULL,
    saving_percentage DOUBLE NOT NULL DEFAULT 0.1,
    FOREIGN KEY (store_id) REFERENCES store(id),
    INDEX idx_store_income_date (income_date)
);