package com.fooddelivery.infrastructure.persistence.adapter;

import com.fooddelivery.application.port.GuestSessionRepositoryPort;
import com.fooddelivery.domain.model.GuestSession;
import com.fooddelivery.infrastructure.persistence.repository.GuestSessionMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GuestSessionRepositoryAdapter implements GuestSessionRepositoryPort {

    private final GuestSessionMongoRepository guestSessionMongoRepository;

    @Override
    public GuestSession save(GuestSession guestSession) {
        return guestSessionMongoRepository.save(guestSession);
    }

    @Override
    public Optional<GuestSession> findByGuestSessionId(String guestSessionId) {
        return guestSessionMongoRepository.findByGuestSessionId(guestSessionId);
    }

    @Override
    public void deleteExpiredSessions() {
        guestSessionMongoRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}
