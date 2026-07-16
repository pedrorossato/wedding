package com.pedrogio.wedding.gift;

import java.math.BigDecimal;
import java.time.Instant;

public record GiftResponse(
    Long id,
    String name,
    String description,
    BigDecimal value,
    String imageUrl,
    Instant createdAt,
    Instant updatedAt
) {}
