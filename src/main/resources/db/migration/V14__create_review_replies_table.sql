CREATE TABLE IF NOT EXISTS review_replies (
    id VARCHAR(36) SERIAL PRIMARY KEY,
    review_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    reply_content TEXT NOT NULL,
    rating INTEGER NOT NULL,
    reply_created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    reply_update TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    FOREIGN KEY (review_id) REFERENCES reviews(id),
    FOREIGN KEY (user_id) REFERENCES _user(id)
);