CREATE TABLE IF NOT EXISTS promotions (
    id VARCHAR(36) SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    discount_type VARCHAR(50) NOT NULL, -- 'PERCENTAGE' or 'FIXED'
    discount_value DECIMAL(10, 2) NOT NULL,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);
