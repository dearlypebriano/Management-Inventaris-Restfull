CREATE TABLE IF NOT EXISTS products (
    id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(300) NOT NULL,
    description TEXT NOT NULL,
    price NUMERIC(38, 2) NOT NULL,
    quantity INTEGER NOT NULL,
    viewers INTEGER NOT NULL DEFAULT 0,
    rating VARCHAR(10) DEFAULT '0.0.0.0.0',
    image_urls TEXT[] NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    uploaded_by VARCHAR(36) NOT NULL,
    version INTEGER DEFAULT 0,
    timezone_label VARCHAR(255) DEFAULT '',
    FOREIGN KEY (uploaded_by) REFERENCES _user(id)
);