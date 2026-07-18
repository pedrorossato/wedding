package com.pedrogio.wedding.guest;

import com.pedrogio.wedding.event.EventConfig;
import com.pedrogio.wedding.event.EventConfigRepository;
import com.pedrogio.wedding.gift.GiftPurchaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GuestService {

    private final GuestRepository repository;
    private final EventConfigRepository eventConfigRepository;
    private final GiftPurchaseRepository giftPurchaseRepository;

    public GuestService(GuestRepository repository,
                        EventConfigRepository eventConfigRepository,
                        GiftPurchaseRepository giftPurchaseRepository) {
        this.repository = repository;
        this.eventConfigRepository = eventConfigRepository;
        this.giftPurchaseRepository = giftPurchaseRepository;
    }

    public PageResponse<GuestResponse> listAll(int page, int size, String sort, String order) {
        Sort.Direction direction = Sort.Direction.fromString(order);
        String sortField = sort.equals("status") ? "confirmed" : "name";
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<Guest> pageResult = repository.findAll(pageable);
        List<GuestResponse> content = pageResult.getContent().stream()
            .map(this::toResponse)
            .toList();
        return new PageResponse<>(content, pageResult.getNumber(), pageResult.getSize(),
            pageResult.getTotalElements(), pageResult.getTotalPages());
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

        if (giftPurchaseRepository.existsByGuestId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Nao e possivel remover convidado com compras de presentes vinculadas");
        }

        repository.delete(guest);
    }

    public Guest findByUuid(UUID uuid) {
        return repository.findByUuid(uuid)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Convite nao encontrado"));
    }

    public InviteResponse confirm(UUID uuid, ConfirmRequest request) {
        Guest guest = repository.findByUuid(uuid)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Convite nao encontrado"));

        if (guest.getConfirmed() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Presenca ja confirmada anteriormente");
        }

        EventConfig event = eventConfigRepository.findTopByOrderByIdAsc()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento nao configurado"));

        if (LocalDateTime.now().isAfter(event.getRsvpDeadline())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prazo de confirmacao encerrado");
        }

        guest.setConfirmed(request.confirmed());
        guest.setConfirmedAt(Instant.now());
        repository.save(guest);

        return toInviteResponse(guest, event);
    }

    private InviteResponse toInviteResponse(Guest guest, EventConfig event) {
        return new InviteResponse(
            guest.getName(),
            guest.getUuid(),
            guest.getConfirmed(),
            event.getWeddingDate(),
            event.getRsvpDeadline()
        );
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
