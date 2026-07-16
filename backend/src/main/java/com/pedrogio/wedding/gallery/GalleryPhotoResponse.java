package com.pedrogio.wedding.gallery;

import java.time.Instant;

public record GalleryPhotoResponse(
    Long id,
    String imageUrl,
    Integer sortOrder,
    Instant createdAt
) {}
