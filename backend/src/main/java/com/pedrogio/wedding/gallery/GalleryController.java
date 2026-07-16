package com.pedrogio.wedding.gallery;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class GalleryController {

    private final GalleryService service;

    public GalleryController(GalleryService service) {
        this.service = service;
    }

    @GetMapping("/api/gallery")
    public ResponseEntity<List<GalleryPhotoResponse>> listPublic() {
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/api/admin/gallery")
    public ResponseEntity<List<GalleryPhotoResponse>> listAll() {
        return ResponseEntity.ok(service.listAll());
    }

    @PostMapping(value = "/api/admin/gallery", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<GalleryPhotoResponse>> upload(@RequestPart List<MultipartFile> files) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.upload(files));
    }

    @PutMapping("/api/admin/gallery/reorder")
    public ResponseEntity<List<GalleryPhotoResponse>> reorder(@Valid @RequestBody GalleryReorderRequest request) {
        return ResponseEntity.ok(service.reorder(request));
    }

    @DeleteMapping("/api/admin/gallery/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
