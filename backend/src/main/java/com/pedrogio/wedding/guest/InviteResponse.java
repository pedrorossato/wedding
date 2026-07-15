package com.pedrogio.wedding.guest;

import java.time.Instant;
import java.util.UUID;

public record InviteResponse(
    String name,
    UUID uuid,
    Boolean confirmed,
    Instant weddingDate,
    Instant rsvpDeadline
) {}
