package com.pedrogio.wedding.gallery;

import com.pedrogio.wedding.config.S3StorageService;
import com.pedrogio.wedding.config.S3StorageService.ImageUpload;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class GalleryService {

    private final GalleryPhotoRepository repository;
    private final S3StorageService s3;

    public GalleryService(GalleryPhotoRepository repository, S3StorageService s3) {
        this.repository = repository;
        this.s3 = s3;
    }

    public List<GalleryPhotoResponse> listAll() {
        return repository.findAllByOrderBySortOrderAsc().stream()
            .map(this::toResponse)
            .toList();
    }

    public List<GalleryPhotoResponse> upload(List<MultipartFile> files) {
        int currentMax = repository.findAll().stream()
            .mapToInt(GalleryPhoto::getSortOrder)
            .max()
            .orElse(-1);

        List<GalleryPhotoResponse> results = new ArrayList<>();
        int order = currentMax + 1;

        for (MultipartFile file : files) {
            ImageUpload upload = s3.upload("gallery", file);

            GalleryPhoto photo = GalleryPhoto.builder()
                .s3Key(upload.s3Key())
                .imageUrl(upload.imageUrl())
                .sortOrder(order++)
                .build();

            repository.save(photo);
            results.add(toResponse(photo));
        }

        return results;
    }

    public List<GalleryPhotoResponse> reorder(GalleryReorderRequest request) {
        List<Long> ids = request.orderedIds();

        for (int i = 0; i < ids.size(); i++) {
            Long id = ids.get(i);
            GalleryPhoto photo = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Foto nao encontrada: " + id));
            photo.setSortOrder(i);
            repository.save(photo);
        }

        return listAll();
    }

    public void delete(Long id) {
        GalleryPhoto photo = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Foto nao encontrada"));

        s3.delete(photo.getS3Key());
        repository.delete(photo);
    }

    private GalleryPhotoResponse toResponse(GalleryPhoto photo) {
        return new GalleryPhotoResponse(
            photo.getId(),
            photo.getImageUrl(),
            photo.getSortOrder(),
            photo.getCreatedAt()
        );
    }
}
