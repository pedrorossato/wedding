package com.pedrogio.wedding.event;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EventUpdateRequest(
    @NotNull LocalDateTime weddingDate,
    @NotNull LocalDateTime rsvpDeadline
) {}
