package com.pedrogio.wedding.guest;

import jakarta.validation.constraints.NotNull;

public record ConfirmRequest(@NotNull Boolean confirmed) {}
