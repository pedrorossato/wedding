CREATE TABLE event_config
(
    id            SERIAL PRIMARY KEY,
    wedding_date  TIMESTAMP NOT NULL,
    rsvp_deadline TIMESTAMP NOT NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP NOT NULL DEFAULT NOW()
);
