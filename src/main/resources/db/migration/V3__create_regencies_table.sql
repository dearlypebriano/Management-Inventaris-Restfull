CREATE TABLE IF NOT EXISTS regencies (
    id CHAR(4) SERIAL PRIMARY KEY,
    province_id CHAR(2),
    name VARCHAR(255),
    FOREIGN KEY (province_id) REFERENCES provinces(id)
);