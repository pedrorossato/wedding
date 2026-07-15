package com.pedrogio.wedding.guest;

import java.time.Instant;
import java.util.UUID;

public record GuestResponse(
    Long id,
    String name,
    UUID uuid,
    Boolean confirmed,
    Instant confirmedAt,
    Instant createdAt,
    Instant updatedAt
) {}
