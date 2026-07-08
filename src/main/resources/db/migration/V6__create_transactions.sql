CREATE TABLE IF NOT EXISTS orders (
    id                  BIGSERIAL PRIMARY KEY,
    rfq_id              BIGINT          NOT NULL UNIQUE REFERENCES rfqs(id),
    quote_id            BIGINT          NOT NULL UNIQUE REFERENCES quotes(id),
    buyer_user_id       BIGINT          NOT NULL REFERENCES users(id),
    seller_user_id      BIGINT          NOT NULL REFERENCES users(id),
    seller_business_id  BIGINT          NOT NULL REFERENCES businesses(id),
    quantity            INTEGER         NOT NULL,
    price_per_unit      NUMERIC(12,2)   NOT NULL,
    total_amount        NUMERIC(12,2)   NOT NULL,
    unit                VARCHAR(50)     NOT NULL,
    delivery_location   VARCHAR(255),
    status              VARCHAR(20)     NOT NULL DEFAULT 'CONFIRMED',
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP
);

CREATE TABLE IF NOT EXISTS payments (
    id                  BIGSERIAL PRIMARY KEY,
    order_id            BIGINT          NOT NULL UNIQUE REFERENCES orders(id),
    razorpay_order_id   VARCHAR(100)    UNIQUE,
    razorpay_payment_id VARCHAR(100)    UNIQUE,
    razorpay_signature  VARCHAR(500),
    amount              NUMERIC(12,2)   NOT NULL,
    currency            VARCHAR(10)     NOT NULL DEFAULT 'INR',
    status              VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP
);

CREATE TABLE IF NOT EXISTS invoices (
    id              BIGSERIAL PRIMARY KEY,
    invoice_number  VARCHAR(50)     NOT NULL UNIQUE,
    order_id        BIGINT          NOT NULL REFERENCES orders(id),
    buyer_user_id   BIGINT          NOT NULL REFERENCES users(id),
    seller_business_id BIGINT       NOT NULL REFERENCES businesses(id),
    amount          NUMERIC(12,2)   NOT NULL,
    gst_amount      NUMERIC(12,2)   NOT NULL DEFAULT 0,
    total_amount    NUMERIC(12,2)   NOT NULL,
    currency        VARCHAR(10)     NOT NULL DEFAULT 'INR',
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);