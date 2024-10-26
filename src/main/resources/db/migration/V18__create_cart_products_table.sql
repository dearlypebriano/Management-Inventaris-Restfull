CREATE TABLE IF NOT EXISTS cart_products (
    cart_id VARCHAR(255) SERIAL NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (cart_id, product_id),
    CONSTRAINT fk_cart
        FOREIGN KEY(cart_id)
        REFERENCES carts(cart_id),
    CONSTRAINT fk_product
        FOREIGN KEY(product_id)
        REFERENCES products(id)
);