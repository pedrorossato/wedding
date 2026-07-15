package com.pedrogio.wedding.gift;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GiftPurchaseRepository extends JpaRepository<GiftPurchase, Long> {

    List<GiftPurchase> findByGiftId(Long giftId);

    List<GiftPurchase> findByGuestId(Long guestId);

    Optional<GiftPurchase> findByPaymentIntentId(String paymentIntentId);

    long countByGiftIdAndPaidTrue(Long giftId);
}
