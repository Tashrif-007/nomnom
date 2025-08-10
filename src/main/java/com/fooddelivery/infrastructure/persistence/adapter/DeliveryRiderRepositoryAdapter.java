package com.fooddelivery.infrastructure.persistence.adapter;

import com.fooddelivery.application.port.DeliveryRiderRepositoryPort;
import com.fooddelivery.domain.model.DeliveryRider;
import com.fooddelivery.infrastructure.persistence.repository.DeliveryRiderMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DeliveryRiderRepositoryAdapter implements DeliveryRiderRepositoryPort {

    private final DeliveryRiderMongoRepository deliveryRiderMongoRepository;

    @Override
    public DeliveryRider save(DeliveryRider deliveryRider) {
        return deliveryRiderMongoRepository.save(deliveryRider);
    }

    @Override
    public Optional<DeliveryRider> findById(String id) {
        return deliveryRiderMongoRepository.findById(id);
    }

    @Override
    public Optional<DeliveryRider> findByUserId(String userId) {
        return deliveryRiderMongoRepository.findByUserId(userId);
    }

    @Override
    public List<DeliveryRider> findByStatusAndIsActive(DeliveryRider.RiderStatus status, boolean isActive) {
        return deliveryRiderMongoRepository.findByStatusAndIsActive(status, isActive);
    }
}
