package com.pedrogio.wedding.stats;

public record StatsResponse(
    long totalGuests,
    long confirmedGuests,
    long totalGifts,
    long giftedGifts,
    long totalGiftedValue,
    long totalPhotos
) {}
