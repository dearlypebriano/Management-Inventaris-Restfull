CREATE TABLE IF NOT EXISTS carts (
    cart_id VARCHAR(255) SERIAL PRIMARY KEY,
    delivery_address VARCHAR(255) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    CONSTRAINT fk_user
        FOREIGN KEY(user_id)
        REFERENCES _user(id)
);