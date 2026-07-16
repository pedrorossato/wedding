package com.pedrogio.wedding.payment;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PaymentCreateRequest(
    @NotNull Long giftId,
    @NotNull UUID guestUuid
) {}
