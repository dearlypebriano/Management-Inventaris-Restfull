CREATE TABLE IF NOT EXISTS categories (
    id VARCHAR(36) SERIAL PRIMARY KEY,
    category_name VARCHAR(255) NOT NULL,
    description TEXT,
    image_url VARCHAR(255),
    is_constant BOOLEAN
);