package com.pedrogio.wedding.guest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GuestRepository extends JpaRepository<Guest, Long> {

    Optional<Guest> findByUuid(UUID uuid);

    boolean existsByUuid(UUID uuid);

    long countByConfirmed(Boolean confirmed);
}
