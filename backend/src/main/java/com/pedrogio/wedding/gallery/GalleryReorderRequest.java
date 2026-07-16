package com.pedrogio.wedding.gallery;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record GalleryReorderRequest(@NotNull List<Long> orderedIds) {}
