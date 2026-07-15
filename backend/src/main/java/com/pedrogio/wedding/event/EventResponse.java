package com.pedrogio.wedding.event;

import java.time.Instant;

public record EventResponse(
    Long id,
    Instant weddingDate,
    Instant rsvpDeadline
) {}
