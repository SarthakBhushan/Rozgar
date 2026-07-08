CREATE TABLE IF NOT EXISTS rfqs (
    id                       BIGSERIAL PRIMARY KEY,
    title                    VARCHAR(200)    NOT NULL,
    description              VARCHAR(2000)   NOT NULL,
    unit                     VARCHAR(50)     NOT NULL,
    quantity                 INTEGER         NOT NULL,
    target_price             NUMERIC(12,2),
    delivery_location        VARCHAR(255),
    deadline                 TIMESTAMP,
    buyer_user_id            BIGINT          NOT NULL REFERENCES users(id),
    buyer_business_id        BIGINT          REFERENCES businesses(id),
    target_seller_business_id BIGINT         REFERENCES businesses(id),
    catalog_item_id          BIGINT          REFERENCES catalog_items(id),
    status                   VARCHAR(20)     NOT NULL DEFAULT 'OPEN',
    created_at               TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at               TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_rfqs_buyer  ON rfqs(buyer_user_id);
CREATE INDEX IF NOT EXISTS idx_rfqs_status ON rfqs(status);

CREATE TABLE IF NOT EXISTS quotes (
    id                  BIGSERIAL PRIMARY KEY,
    rfq_id              BIGINT          NOT NULL REFERENCES rfqs(id),
    seller_user_id      BIGINT          NOT NULL REFERENCES users(id),
    seller_business_id  BIGINT          NOT NULL REFERENCES businesses(id),
    price_per_unit      NUMERIC(12,2)   NOT NULL,
    available_quantity  INTEGER         NOT NULL,
    note                VARCHAR(1000),
    valid_until         TIMESTAMP,
    status              VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_quotes_rfq ON quotes(rfq_id);