CREATE TABLE IF NOT EXISTS user_coupon (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    coupon_id UUID NOT NULL,
    used_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES _user(id),
    FOREIGN KEY (coupon_id) REFERENCES coupons(id)
);