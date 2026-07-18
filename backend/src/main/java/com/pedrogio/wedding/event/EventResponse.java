package com.pedrogio.wedding.event;

import java.time.LocalDateTime;

public record EventResponse(
    Long id,
    LocalDateTime weddingDate,
    LocalDateTime rsvpDeadline
) {}
