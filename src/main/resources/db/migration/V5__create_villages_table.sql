CREATE TABLE IF NOT EXISTS villages (
    id CHAR(10) SERIAL PRIMARY KEY,
    district_id CHAR(7),
    name VARCHAR(255),
    FOREIGN KEY (district_id) REFERENCES districts(id)
);