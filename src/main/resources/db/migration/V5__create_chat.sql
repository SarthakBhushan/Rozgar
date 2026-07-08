CREATE TABLE IF NOT EXISTS conversation_threads (
    id                  BIGSERIAL PRIMARY KEY,
    rfq_id              BIGINT  NOT NULL UNIQUE REFERENCES rfqs(id),
    buyer_user_id       BIGINT  NOT NULL REFERENCES users(id),
    seller_user_id      BIGINT  NOT NULL REFERENCES users(id),
    buyer_business_id   BIGINT,
    seller_business_id  BIGINT  NOT NULL REFERENCES businesses(id),
    created_at          TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS messages (
    id          BIGSERIAL PRIMARY KEY,
    thread_id   BIGINT          NOT NULL REFERENCES conversation_threads(id),
    sender_user_id  BIGINT      NOT NULL REFERENCES users(id),
    sender_name VARCHAR(100)    NOT NULL,
    content     VARCHAR(2000)   NOT NULL,
    read        BOOLEAN         NOT NULL DEFAULT FALSE,
    sent_at     TIMESTAMP       NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_messages_thread ON messages(thread_id);