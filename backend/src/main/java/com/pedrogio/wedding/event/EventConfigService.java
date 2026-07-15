package com.pedrogio.wedding.event;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EventConfigService {

    private final EventConfigRepository repository;

    public EventConfigService(EventConfigRepository repository) {
        this.repository = repository;
    }

    public EventResponse getConfig() {
        EventConfig config = repository.findTopByOrderByIdAsc()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento nao configurado"));
        return toResponse(config);
    }

    public EventResponse update(EventUpdateRequest request) {
        if (!request.rsvpDeadline().isBefore(request.weddingDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Data limite de confirmacao deve ser anterior a data do casamento");
        }

        EventConfig config = repository.findTopByOrderByIdAsc()
            .orElseGet(EventConfig::new);

        config.setWeddingDate(request.weddingDate());
        config.setRsvpDeadline(request.rsvpDeadline());
        repository.save(config);

        return toResponse(config);
    }

    private EventResponse toResponse(EventConfig config) {
        return new EventResponse(config.getId(), config.getWeddingDate(), config.getRsvpDeadline());
    }
}
