package com.pedrogio.wedding.gallery;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GalleryPhotoRepository extends JpaRepository<GalleryPhoto, Long> {

    List<GalleryPhoto> findAllByOrderBySortOrderAsc();
}
