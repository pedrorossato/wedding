CREATE TABLE gift_purchases
(
    id                SERIAL PRIMARY KEY,
    gift_id           BIGINT       NOT NULL REFERENCES gifts (id),
    guest_id          BIGINT       NOT NULL REFERENCES guests (id),
    paid              BOOLEAN      NOT NULL DEFAULT FALSE,
    payment_intent_id VARCHAR(255),
    created_at        TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP    NOT NULL DEFAULT NOW()
);
