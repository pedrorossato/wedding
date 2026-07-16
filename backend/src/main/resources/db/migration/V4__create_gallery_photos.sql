CREATE TABLE gallery_photos
(
    id         BIGSERIAL PRIMARY KEY,
    s3_key     VARCHAR(500) NOT NULL,
    image_url  VARCHAR(500) NOT NULL,
    sort_order INTEGER      NOT NULL DEFAULT 0,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW()
);
