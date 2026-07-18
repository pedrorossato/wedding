package com.pedrogio.wedding.event;

import com.pedrogio.wedding.infra.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wedding_date", nullable = false)
    private LocalDateTime weddingDate;

    @Column(name = "rsvp_deadline", nullable = false)
    private LocalDateTime rsvpDeadline;
}
