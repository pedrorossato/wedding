package com.pedrogio.wedding.guest;

import com.pedrogio.wedding.event.EventConfigService;
import com.pedrogio.wedding.event.EventResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class GuestController {

    private final GuestService guestService;
    private final EventConfigService eventService;

    public GuestController(GuestService guestService, EventConfigService eventService) {
        this.guestService = guestService;
        this.eventService = eventService;
    }

    @GetMapping("/admin/guests")
    public ResponseEntity<List<GuestResponse>> listAll() {
        return ResponseEntity.ok(guestService.listAll());
    }

    @GetMapping("/admin/guests/{id}")
    public ResponseEntity<GuestResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(guestService.getById(id));
    }

    @PostMapping("/admin/guests")
    public ResponseEntity<GuestResponse> create(@Valid @RequestBody GuestCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guestService.create(request));
    }

    @PutMapping("/admin/guests/{id}")
    public ResponseEntity<GuestResponse> update(@PathVariable Long id, @Valid @RequestBody GuestUpdateRequest request) {
        return ResponseEntity.ok(guestService.update(id, request));
    }

    @DeleteMapping("/admin/guests/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        guestService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/invite/{uuid}")
    public ResponseEntity<InviteResponse> getInvite(@PathVariable UUID uuid) {
        Guest guest = guestService.findByUuid(uuid);

        EventResponse event;
        try {
            event = eventService.getConfig();
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento nao configurado");
        }

        return ResponseEntity.ok(new InviteResponse(
            guest.getName(),
            guest.getUuid(),
            guest.getConfirmed(),
            event.weddingDate(),
            event.rsvpDeadline()
        ));
    }

    @PostMapping("/invite/{uuid}/confirm")
    public ResponseEntity<InviteResponse> confirm(@PathVariable UUID uuid,
                                                   @Valid @RequestBody ConfirmRequest request) {
        return ResponseEntity.ok(guestService.confirm(uuid, request));
    }
}
