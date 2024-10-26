CREATE TABLE IF NOT EXISTS orders (
    order_id VARCHAR(255) SERIAL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    quantity INTEGER NOT NULL,
    order_date VARCHAR(255) NOT NULL,
    status_order VARCHAR(255) NOT NULL,
    cart_id VARCHAR(255) NOT NULL,
    CONSTRAINT fk_user
        FOREIGN KEY(user_id)
        REFERENCES _user(id),
    CONSTRAINT fk_product
        FOREIGN KEY(product_id)
        REFERENCES products(id),
    CONSTRAINT fk_cart
        FOREIGN KEY(cart_id)
        REFERENCES carts(cart_id)
);