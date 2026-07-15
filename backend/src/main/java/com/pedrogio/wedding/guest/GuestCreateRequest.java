package com.pedrogio.wedding.guest;

import jakarta.validation.constraints.NotBlank;

public record GuestCreateRequest(@NotBlank String name) {}
