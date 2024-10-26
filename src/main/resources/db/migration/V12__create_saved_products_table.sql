CREATE TABLE IF NOT EXISTS saved_products (
    id VARCHAR(36) SERIAL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES _user(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);