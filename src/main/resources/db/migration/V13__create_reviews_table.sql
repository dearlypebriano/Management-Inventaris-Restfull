CREATE TABLE IF NOT EXISTS reviews (
    id VARCHAR(36) SERIAL PRIMARY KEY,
    product_id VARCHAR(36),
    user_id VARCHAR(36),
    rating INTEGER NOT NULL,
    comment TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (user_id) REFERENCES _user(id)
);