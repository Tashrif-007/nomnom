package com.fooddelivery.infrastructure.persistence.repository;

import com.fooddelivery.domain.model.DeliveryRider;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRiderMongoRepository extends MongoRepository<DeliveryRider, String> {
    Optional<DeliveryRider> findByUserId(String userId);
    List<DeliveryRider> findByStatusAndIsActive(DeliveryRider.RiderStatus status, boolean isActive);
}
