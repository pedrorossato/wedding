package com.pedrogio.wedding.guest;

import java.time.LocalDateTime;
import java.util.UUID;

public record InviteResponse(
    String name,
    UUID uuid,
    Boolean confirmed,
    LocalDateTime weddingDate,
    LocalDateTime rsvpDeadline
) {}
