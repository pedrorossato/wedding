package com.pedrogio.wedding.gift;

import com.pedrogio.wedding.config.S3StorageService;
import com.pedrogio.wedding.config.S3StorageService.ImageUpload;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class GiftService {

    private final GiftRepository repository;
    private final GiftPurchaseRepository giftPurchaseRepository;
    private final S3StorageService s3;

    public GiftService(GiftRepository repository,
                       GiftPurchaseRepository giftPurchaseRepository,
                       S3StorageService s3) {
        this.repository = repository;
        this.giftPurchaseRepository = giftPurchaseRepository;
        this.s3 = s3;
    }

    public List<GiftResponse> listAll() {
        return repository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    public GiftResponse getById(Long id) {
        return repository.findById(id)
            .map(this::toResponse)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Presente nao encontrado"));
    }

    public GiftResponse create(String name, String description, BigDecimal value, MultipartFile image) {
        ImageUpload upload = s3.upload("gifts", image);

        Gift gift = Gift.builder()
            .name(name)
            .description(description)
            .value(value)
            .imageUrl(upload.imageUrl())
            .s3Key(upload.s3Key())
            .build();

        repository.save(gift);
        return toResponse(gift);
    }

    public GiftResponse update(Long id, String name, String description, BigDecimal value, MultipartFile image) {
        Gift gift = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Presente nao encontrado"));

        gift.setName(name);
        gift.setDescription(description);
        gift.setValue(value);

        if (image != null && !image.isEmpty()) {
            s3.delete(gift.getS3Key());
            ImageUpload upload = s3.upload("gifts", image);
            gift.setImageUrl(upload.imageUrl());
            gift.setS3Key(upload.s3Key());
        }

        repository.save(gift);
        return toResponse(gift);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Presente nao encontrado");
        }

        if (giftPurchaseRepository.existsByGiftId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Nao e possivel remover presente com compras vinculadas");
        }

        Gift gift = repository.findById(id).orElseThrow();
        s3.delete(gift.getS3Key());
        repository.delete(gift);
    }

    public List<GiftPurchaseInfo> listPurchases(Long giftId) {
        if (!repository.existsById(giftId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Presente nao encontrado");
        }

        return giftPurchaseRepository.findByGiftIdWithGuest(giftId).stream()
            .map(gp -> new GiftPurchaseInfo(
                gp.getId(),
                gp.getGuest().getName(),
                gp.getPaid(),
                gp.getPaymentIntentId()
            ))
            .toList();
    }

    private GiftResponse toResponse(Gift gift) {
        return new GiftResponse(
            gift.getId(),
            gift.getName(),
            gift.getDescription(),
            gift.getValue(),
            gift.getImageUrl(),
            gift.getCreatedAt(),
            gift.getUpdatedAt()
        );
    }
}
