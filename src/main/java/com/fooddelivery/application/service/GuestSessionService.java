package com.fooddelivery.application.service;

import com.fooddelivery.domain.model.GuestSession;

public interface GuestSessionService {
    GuestSession createGuestSession();
    GuestSession getGuestSession(String guestSessionId);
    boolean isValidGuestSession(String guestSessionId);
}
