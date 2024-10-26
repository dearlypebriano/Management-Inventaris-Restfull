CREATE TABLE IF NOT EXISTS districts (
    id CHAR(7) SERIAL PRIMARY KEY,
    regency_id CHAR(4),
    name VARCHAR(255),
    FOREIGN KEY (regency_id) REFERENCES regencies(id)
);