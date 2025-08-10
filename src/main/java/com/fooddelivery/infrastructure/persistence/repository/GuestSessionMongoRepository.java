package com.fooddelivery.infrastructure.persistence.repository;

import com.fooddelivery.domain.model.GuestSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface GuestSessionMongoRepository extends MongoRepository<GuestSession, String> {
    Optional<GuestSession> findByGuestSessionId(String guestSessionId);
    
    @Query("{'expiresAt': {$lt: ?0}}")
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}
