CREATE TABLE IF NOT EXISTS businesses (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100)  NOT NULL,
    description     VARCHAR(1000),
    business_type   VARCHAR(30)   NOT NULL,
    gst_number      VARCHAR(15)   UNIQUE,
    pan_number      VARCHAR(10)   UNIQUE,
    city            VARCHAR(100)  NOT NULL,
    state           VARCHAR(100)  NOT NULL,
    pincode         VARCHAR(10),
    address         VARCHAR(500),
    phone           VARCHAR(15),
    website         VARCHAR(255),
    status          VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    owner_id        BIGINT        NOT NULL REFERENCES users(id),
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_businesses_owner ON businesses(owner_id);
CREATE INDEX IF NOT EXISTS idx_businesses_city  ON businesses(city);