CREATE TABLE IF NOT EXISTS _user (
    id VARCHAR(36) NOT NULL UNIQUE SERIAL PRIMARY KEY,
    firstname VARCHAR(300) NOT NULL,
    lastname VARCHAR(300) NOT NULL,
    bioProfile VARCHAR(300) NOT NULL DEFAULT 'No Bio Yet!',
    username_user VARCHAR(255) NOT NULL,
    phone BIGINT DEFAULT 0 NOT NULL,
    email VARCHAR(300) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255),
    gender_id INT,
    province_id CHAR(2),
    regency_id CHAR(4),
    district_id CHAR(7),
    village_id CHAR(10),
    image_url VARCHAR(255),
    ip_address VARCHAR(255) NOT NULL,
    mac_address VARCHAR(255) NOT NULL,
    user_agent VARCHAR(255) NOT NULL,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    followers_count INT NOT NULL DEFAULT 0,
    following_count INT NOT NULL DEFAULT 0,
    likes_count INT NOT NULL DEFAULT 0,
    liked_users_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    timezone_label VARCHAR(255) NOT NULL,
    FOREIGN KEY (gender_id) REFERENCES gender(id),
    FOREIGN KEY (province_id) REFERENCES provinces(id),
    FOREIGN KEY (regency_id) REFERENCES regencies(id),
    FOREIGN KEY (district_id) REFERENCES districts(id),
    FOREIGN KEY (village_id) REFERENCES villages(id)
);

CREATE TABLE IF NOT EXISTS user_likes (
    liker_id UUID NOT NULL,
    liked_id UUID NOT NULL,
    PRIMARY KEY (liker_id, liked_id),
    CONSTRAINT fk_liker FOREIGN KEY (liker_id) REFERENCES _user (id) ON DELETE CASCADE,
    CONSTRAINT fk_liked FOREIGN KEY (liked_id) REFERENCES _user (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_followers (
    follower_id UUID NOT NULL,
    followed_id UUID NOT NULL,
    PRIMARY KEY (follower_id, followed_id),
    CONSTRAINT fk_follower FOREIGN KEY (follower_id) REFERENCES _user (id) ON DELETE CASCADE,
    CONSTRAINT fk_followed FOREIGN KEY (followed_id) REFERENCES _user (id) ON DELETE CASCADE
);