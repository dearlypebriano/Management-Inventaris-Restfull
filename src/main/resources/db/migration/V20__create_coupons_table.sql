CREATE TABLE coupons (
    id VARCHAR(36) SERIAL PRIMARY KEY,
    code VARCHAR(100) UNIQUE NOT NULL,
    discount_type VARCHAR(50) NOT NULL, -- 'PERCENTAGE' or 'FIXED'
    discount_value DECIMAL(10, 2),
    max_uses INT DEFAULT 1,
    uses_count INT DEFAULT 0,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    is_expired BOOLEAN DEFAULT FALSE
);
