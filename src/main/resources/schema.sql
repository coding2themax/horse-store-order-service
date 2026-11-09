-- Finished feed purchases. A row is written only when the saga ends.
CREATE TABLE IF NOT EXISTS feed_purchase (
    saga_id        UUID PRIMARY KEY,
    customer_id    VARCHAR(100)   NOT NULL,
    feed_id        VARCHAR(100)   NOT NULL,
    quantity       INTEGER        NOT NULL CHECK (quantity > 0),
    unit_price     NUMERIC(12, 2),
    total_price    NUMERIC(12, 2),
    payment_id     VARCHAR(100),
    status         VARCHAR(20)    NOT NULL,
    failure_reason VARCHAR(500),
    completed_at   TIMESTAMPTZ    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_feed_purchase_customer ON feed_purchase (customer_id);
