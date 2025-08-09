package com.fooddelivery.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "guest_sessions")
public class GuestSession {
    @Id
    private String id;
    private String guestSessionId;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
