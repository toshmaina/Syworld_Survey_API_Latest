-- ─────────────────────────────────────────────────────────────────
--  Migration: Add users table for JWT authentication
--  Run this against your existing sky_survey_db
-- ─────────────────────────────────────────────────────────────────



CREATE TABLE IF NOT EXISTS users (
                                     id         BIGINT       NOT NULL AUTO_INCREMENT,
                                     username   VARCHAR(100) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    role       ENUM('ADMIN','USER') NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uq_users_email    (email),
    UNIQUE KEY uq_users_username (username)
    );

-- Default admin user  (password: Admin@1234)
INSERT IGNORE INTO users (username, email, password, role) VALUES
  ('admin', 'admin@skyworld.com', '$2a$10$JiX4TyiqASlypV1MZSS0S.u/cU8qmxw6nL/xyc4qMnQs0UDGr0Qvy', 'ADMIN');

-- Default regular user  (password: User@1234)
INSERT IGNORE INTO users (username, email, password, role) VALUES
  ('user', 'user@skyworld.com', '$2a$10$QfPPOsmD/OiFPBHWSNSwEe6c4aSTv4kCeG97oVqD3flU..ajmciiC', 'USER');