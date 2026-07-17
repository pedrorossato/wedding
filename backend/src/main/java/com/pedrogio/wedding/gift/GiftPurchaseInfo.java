package com.pedrogio.wedding.gift;

public record GiftPurchaseInfo(
    Long id,
    String guestName,
    Boolean paid,
    String paymentIntentId
) {}
