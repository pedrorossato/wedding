package com.pedrogio.wedding.gift;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class GiftController {

    private final GiftService service;

    public GiftController(GiftService service) {
        this.service = service;
    }

    @GetMapping("/gifts")
    public ResponseEntity<List<GiftResponse>> listPublic() {
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/admin/gifts")
    public ResponseEntity<List<GiftResponse>> listAll() {
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/admin/gifts/{id}")
    public ResponseEntity<GiftResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping(value = "/admin/gifts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GiftResponse> create(
            @RequestParam @NotBlank String name,
            @RequestParam(required = false) String description,
            @RequestParam @NotNull BigDecimal value,
            @RequestPart MultipartFile image) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(service.create(name, description, value, image));
    }

    @PutMapping(value = "/admin/gifts/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GiftResponse> update(
            @PathVariable Long id,
            @RequestParam @NotBlank String name,
            @RequestParam(required = false) String description,
            @RequestParam @NotNull BigDecimal value,
            @RequestPart(required = false) MultipartFile image) {
        return ResponseEntity.ok(service.update(id, name, description, value, image));
    }

    @DeleteMapping("/admin/gifts/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/gifts/{id}/purchases")
    public ResponseEntity<List<GiftPurchaseInfo>> listPurchases(@PathVariable Long id) {
        return ResponseEntity.ok(service.listPurchases(id));
    }
}
