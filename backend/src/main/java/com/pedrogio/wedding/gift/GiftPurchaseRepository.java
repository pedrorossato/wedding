package com.pedrogio.wedding.gift;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface GiftPurchaseRepository extends JpaRepository<GiftPurchase, Long> {

    List<GiftPurchase> findByGiftId(Long giftId);

    List<GiftPurchase> findByGuestId(Long guestId);

    Optional<GiftPurchase> findByPaymentIntentId(String paymentIntentId);

    long countByGiftIdAndPaidTrue(Long giftId);

    @Query("SELECT COALESCE(SUM(g.value), 0) FROM GiftPurchase gp JOIN gp.gift g WHERE gp.paid = true")
    BigDecimal sumPaidAmounts();

    @Query("SELECT COUNT(DISTINCT gp.gift.id) FROM GiftPurchase gp WHERE gp.paid = true")
    long countGiftsWithPaidPurchases();

    boolean existsByGuestId(Long guestId);

    boolean existsByGiftId(Long giftId);

    @Query("SELECT gp FROM GiftPurchase gp JOIN FETCH gp.guest WHERE gp.gift.id = :giftId")
    List<GiftPurchase> findByGiftIdWithGuest(@org.springframework.data.repository.query.Param("giftId") Long giftId);
}
