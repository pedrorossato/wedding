package com.pedrogio.wedding.payment;

import com.pedrogio.wedding.config.WeddingProperties;
import com.pedrogio.wedding.gift.Gift;
import com.pedrogio.wedding.gift.GiftPurchase;
import com.pedrogio.wedding.gift.GiftPurchaseRepository;
import com.pedrogio.wedding.gift.GiftRepository;
import com.pedrogio.wedding.guest.Guest;
import com.pedrogio.wedding.guest.GuestRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService {

    private final WeddingProperties properties;
    private final GiftRepository giftRepository;
    private final GuestRepository guestRepository;
    private final GiftPurchaseRepository purchaseRepository;

    public PaymentService(WeddingProperties properties,
                          GiftRepository giftRepository,
                          GuestRepository guestRepository,
                          GiftPurchaseRepository purchaseRepository) {
        this.properties = properties;
        this.giftRepository = giftRepository;
        this.guestRepository = guestRepository;
        this.purchaseRepository = purchaseRepository;
    }

    @PostConstruct
    void init() {
        Stripe.apiKey = properties.getStripe().getSecretKey();
    }

    public PaymentCreateResponse create(PaymentCreateRequest request) {
        Gift gift = giftRepository.findById(request.giftId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Presente nao encontrado"));

        Guest guest = guestRepository.findByUuid(request.guestUuid())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Convidado nao encontrado"));

        long amountInCents = gift.getValue().multiply(BigDecimal.valueOf(100)).longValue();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
            .setAmount(amountInCents)
            .setCurrency("brl")
            .setAutomaticPaymentMethods(
                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                    .setEnabled(true)
                    .build()
            )
            .putMetadata("gift_id", gift.getId().toString())
            .putMetadata("guest_id", guest.getId().toString())
            .setDescription(gift.getName())
            .build();

        try {
            PaymentIntent intent = PaymentIntent.create(params);

            GiftPurchase purchase = GiftPurchase.builder()
                .gift(gift)
                .guest(guest)
                .paymentIntentId(intent.getId())
                .paid(false)
                .build();
            purchaseRepository.save(purchase);

            return new PaymentCreateResponse(intent.getClientSecret());
        } catch (StripeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Erro no provedor de pagamento: " + e.getMessage());
        }
    }
}
