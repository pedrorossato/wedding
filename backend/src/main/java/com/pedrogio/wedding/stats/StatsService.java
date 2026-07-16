package com.pedrogio.wedding.stats;

import com.pedrogio.wedding.gallery.GalleryPhotoRepository;
import com.pedrogio.wedding.gift.GiftPurchaseRepository;
import com.pedrogio.wedding.gift.GiftRepository;
import com.pedrogio.wedding.guest.GuestRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StatsService {

    private final GuestRepository guestRepository;
    private final GiftRepository giftRepository;
    private final GiftPurchaseRepository purchaseRepository;
    private final GalleryPhotoRepository photoRepository;

    public StatsService(GuestRepository guestRepository,
                        GiftRepository giftRepository,
                        GiftPurchaseRepository purchaseRepository,
                        GalleryPhotoRepository photoRepository) {
        this.guestRepository = guestRepository;
        this.giftRepository = giftRepository;
        this.purchaseRepository = purchaseRepository;
        this.photoRepository = photoRepository;
    }

    public StatsResponse getStats() {
        long totalGuests = guestRepository.count();
        long confirmedGuests = guestRepository.countByConfirmed(true);
        long totalGifts = giftRepository.count();
        long giftedGifts = purchaseRepository.countGiftsWithPaidPurchases();
        BigDecimal sum = purchaseRepository.sumPaidAmounts();
        long totalGiftedValue = sum != null ? sum.longValue() : 0L;
        long totalPhotos = photoRepository.count();

        return new StatsResponse(
            totalGuests,
            confirmedGuests,
            totalGifts,
            giftedGifts,
            totalGiftedValue,
            totalPhotos
        );
    }
}
