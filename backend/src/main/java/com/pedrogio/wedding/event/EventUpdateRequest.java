package com.pedrogio.wedding.event;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record EventUpdateRequest(
    @NotNull Instant weddingDate,
    @NotNull Instant rsvpDeadline
) {}
