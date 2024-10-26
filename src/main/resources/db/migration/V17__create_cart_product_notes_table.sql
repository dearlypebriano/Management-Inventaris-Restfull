CREATE TABLE IF NOT EXISTS cart_product_notes (
    id VARCHAR(255) SERIAL PRIMARY KEY,
    cart_id VARCHAR(255) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    note TEXT,
    CONSTRAINT fk_cart
        FOREIGN KEY(cart_id)
        REFERENCES carts(cart_id),
    CONSTRAINT fk_product
        FOREIGN KEY(product_id)
        REFERENCES products(id)
);