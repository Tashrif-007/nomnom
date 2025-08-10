package com.fooddelivery.application.port;

import com.fooddelivery.domain.model.DeliveryRider;

import java.util.List;
import java.util.Optional;

public interface DeliveryRiderRepositoryPort {
    DeliveryRider save(DeliveryRider deliveryRider);
    Optional<DeliveryRider> findById(String id);
    Optional<DeliveryRider> findByUserId(String userId);
    List<DeliveryRider> findByStatusAndIsActive(DeliveryRider.RiderStatus status, boolean isActive);
}
