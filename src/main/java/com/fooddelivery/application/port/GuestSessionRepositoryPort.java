package com.fooddelivery.application.port;

import com.fooddelivery.domain.model.GuestSession;

import java.util.Optional;

public interface GuestSessionRepositoryPort {
    GuestSession save(GuestSession guestSession);
    Optional<GuestSession> findByGuestSessionId(String guestSessionId);
    void deleteExpiredSessions();
}
