CREATE TABLE IF NOT EXISTS reservation_time (
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    start_at TIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE(start_at)
    );

CREATE TABLE IF NOT EXISTS member (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    name        VARCHAR(50)     NOT NULL,
    email       VARCHAR(50)    NOT NULL,
    password    VARCHAR(50)     NOT NULL,
    role        VARCHAR(20)     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS store (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    name        VARCHAR(50)    NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS store_manager (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    member_id   BIGINT          NOT NULL,
    store_id    BIGINT          NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES member (id),
    FOREIGN KEY (store_id) REFERENCES store (id),
    UNIQUE (member_id, store_id)
);

CREATE TABLE IF NOT EXISTS theme (
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    name     VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    thumbnail VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE(name)
);

CREATE TABLE IF NOT EXISTS reservation (
    id      BIGINT       NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    date    DATE NOT NULL,
    time_id BIGINT NOT NULL,
    theme_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES member (id),
    FOREIGN KEY (store_id) REFERENCES store (id),
    FOREIGN KEY (time_id) REFERENCES reservation_time (id),
    FOREIGN KEY (theme_id) REFERENCES theme (id),
    UNIQUE (store_id, date, time_id, theme_id)
);

CREATE INDEX IF NOT EXISTS idx_reservation_member_id ON reservation (member_id);
CREATE INDEX IF NOT EXISTS idx_reservation_store_id ON reservation (store_id);
