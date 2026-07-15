package com.pedrogio.wedding.guest;

import jakarta.validation.constraints.NotBlank;

public record GuestUpdateRequest(@NotBlank String name) {}
