CREATE TABLE IF NOT EXISTS categories (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS catalog_items (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(150)    NOT NULL,
    description         VARCHAR(2000),
    item_type           VARCHAR(20)     NOT NULL,
    category_id         BIGINT          REFERENCES categories(id),
    price_per_unit      NUMERIC(12,2)   NOT NULL,
    unit                VARCHAR(50)     NOT NULL,
    min_order_quantity  INTEGER         NOT NULL DEFAULT 1,
    business_id         BIGINT          NOT NULL REFERENCES businesses(id),
    status              VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_catalog_business ON catalog_items(business_id);