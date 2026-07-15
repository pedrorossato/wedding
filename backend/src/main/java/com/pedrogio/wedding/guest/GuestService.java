package com.pedrogio.wedding.guest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class GuestService {

    private final GuestRepository repository;

    public GuestService(GuestRepository repository) {
        this.repository = repository;
    }

    public List<GuestResponse> listAll() {
        return repository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    public GuestResponse getById(Long id) {
        return repository.findById(id)
            .map(this::toResponse)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Convidado nao encontrado"));
    }

    public GuestResponse create(GuestCreateRequest request) {
        Guest guest = Guest.builder()
            .name(request.name())
            .uuid(UUID.randomUUID())
            .build();
        repository.save(guest);
        return toResponse(guest);
    }

    public GuestResponse update(Long id, GuestUpdateRequest request) {
        Guest guest = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Convidado nao encontrado"));
        guest.setName(request.name());
        repository.save(guest);
        return toResponse(guest);
    }

    public void delete(Long id) {
        Guest guest = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Convidado nao encontrado"));

        if (!guest.getGiftPurchases().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Nao e possivel remover convidado com compras de presentes vinculadas");
        }

        repository.delete(guest);
    }

    public Guest findByUuid(UUID uuid) {
        return repository.findByUuid(uuid)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Convite nao encontrado"));
    }

    private GuestResponse toResponse(Guest guest) {
        return new GuestResponse(
            guest.getId(),
            guest.getName(),
            guest.getUuid(),
            guest.getConfirmed(),
            guest.getConfirmedAt(),
            guest.getCreatedAt(),
            guest.getUpdatedAt()
        );
    }
}
