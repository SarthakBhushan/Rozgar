CREATE TABLE IF NOT EXISTS users (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100)  NOT NULL,
    email       VARCHAR(255)  NOT NULL UNIQUE,
    phone       VARCHAR(15),
    password    VARCHAR(255)  NOT NULL,
    role        VARCHAR(30)   NOT NULL,
    enabled     BOOLEAN       NOT NULL DEFAULT TRUE,
    email_verified BOOLEAN    NOT NULL DEFAULT FALSE,
    phone_verified BOOLEAN    NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);