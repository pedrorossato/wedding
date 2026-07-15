package com.pedrogio.wedding.event;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventController {

    private final EventConfigService service;

    public EventController(EventConfigService service) {
        this.service = service;
    }

    @GetMapping("/api/event")
    public ResponseEntity<EventResponse> getPublic() {
        return ResponseEntity.ok(service.getConfig());
    }

    @GetMapping("/api/admin/event")
    public ResponseEntity<EventResponse> getAdmin() {
        return ResponseEntity.ok(service.getConfig());
    }

    @PutMapping("/api/admin/event")
    public ResponseEntity<EventResponse> update(@Valid @RequestBody EventUpdateRequest request) {
        return ResponseEntity.ok(service.update(request));
    }
}
