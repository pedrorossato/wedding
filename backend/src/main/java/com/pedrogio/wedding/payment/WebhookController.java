package com.pedrogio.wedding.payment;

import com.pedrogio.wedding.config.WeddingProperties;
import com.pedrogio.wedding.gift.GiftPurchase;
import com.pedrogio.wedding.gift.GiftPurchaseRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);
    private static final Pattern PI_ID_PATTERN = Pattern.compile("\"id\"\\s*:\\s*\"(pi_[^\"]+)\"");

    private final String webhookSecret;
    private final GiftPurchaseRepository purchaseRepository;

    public WebhookController(WeddingProperties properties, GiftPurchaseRepository purchaseRepository) {
        this.webhookSecret = properties.getStripe().getWebhookSecret();
        this.purchaseRepository = purchaseRepository;
    }

    @PostMapping("/api/webhooks/stripe")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Assinatura de webhook invalida: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        log.info("Webhook recebido: type={}", event.getType());

        if ("payment_intent.succeeded".equals(event.getType())) {
            Matcher matcher = PI_ID_PATTERN.matcher(payload);
            if (matcher.find()) {
                String paymentIntentId = matcher.group(1);
                handlePaymentSucceeded(paymentIntentId);
            } else {
                log.warn("PaymentIntent ID nao encontrado no payload do evento {}", event.getId());
            }
        }

        return ResponseEntity.ok("");
    }

    private void handlePaymentSucceeded(String paymentIntentId) {
        purchaseRepository.findByPaymentIntentId(paymentIntentId)
            .ifPresentOrElse(
                purchase -> {
                    purchase.setPaid(true);
                    purchaseRepository.save(purchase);
                    log.info("Pagamento confirmado: intent={}, gift={}, guest={}",
                        paymentIntentId, purchase.getGift().getId(), purchase.getGuest().getId());
                },
                () -> log.warn("PaymentIntent nao encontrado no banco: {}", paymentIntentId)
            );
    }
}
