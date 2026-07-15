package com.pedrogio.wedding.event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventConfigRepository extends JpaRepository<EventConfig, Long> {

    Optional<EventConfig> findTopByOrderByIdAsc();
}
