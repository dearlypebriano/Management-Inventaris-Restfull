CREATE TABLE IF NOT EXISTS store (
    id VARCHAR(36) PRIMARY KEY,
    store_name VARCHAR(255) NOT NULL UNIQUE,
    province_id VARCHAR(36),
    regency_id VARCHAR(36),
    district_id VARCHAR(36),
    village_id VARCHAR(36),
    street VARCHAR(255) NOT NULL UNIQUE,
    user_id VARCHAR(36) NOT NULL,
    established_since VARCHAR(255) NOT NULL,
    timezone_label VARCHAR(100) NOT NULL,
    CONSTRAINT fk_province_store FOREIGN KEY (province_id) REFERENCES provinces(id),
    CONSTRAINT fk_regency_store FOREIGN KEY (regency_id) REFERENCES regencies(id),
    CONSTRAINT fk_district_store FOREIGN KEY (district_id) REFERENCES districts(id),
    CONSTRAINT fk_village_store FOREIGN KEY (village_id) REFERENCES villages(id),
    CONSTRAINT fk_user_store FOREIGN KEY (user_id) REFERENCES _user(id),
    CONSTRAINT unq_store_user UNIQUE (user_id),
    CONSTRAINT unq_store_street UNIQUE (street),
    CONSTRAINT unq_store_name UNIQUE (store_name)
);

CREATE INDEX idx_store_name ON store (store_name);
