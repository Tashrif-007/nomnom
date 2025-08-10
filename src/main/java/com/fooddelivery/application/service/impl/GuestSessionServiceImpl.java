package com.fooddelivery.application.service.impl;

import com.fooddelivery.application.port.GuestSessionRepositoryPort;
import com.fooddelivery.application.service.GuestSessionService;
import com.fooddelivery.domain.model.GuestSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GuestSessionServiceImpl implements GuestSessionService {

    private final GuestSessionRepositoryPort guestSessionRepository;

    @Override
    public GuestSession createGuestSession() {
        GuestSession guestSession = new GuestSession();
        guestSession.setGuestSessionId(UUID.randomUUID().toString());
        guestSession.setCreatedAt(LocalDateTime.now());
        guestSession.setExpiresAt(LocalDateTime.now().plusHours(1)); // 1 hour expiry

        return guestSessionRepository.save(guestSession);
    }

    @Override
    public GuestSession getGuestSession(String guestSessionId) {
        return guestSessionRepository.findByGuestSessionId(guestSessionId)
                .orElseThrow(() -> new RuntimeException("Guest session not found"));
    }

    @Override
    public boolean isValidGuestSession(String guestSessionId) {
        try {
            GuestSession session = getGuestSession(guestSessionId);
            return session.getExpiresAt().isAfter(LocalDateTime.now());
        } catch (RuntimeException e) {
            return false;
        }
    }
}
