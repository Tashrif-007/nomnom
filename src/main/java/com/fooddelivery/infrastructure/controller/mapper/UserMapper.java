package com.fooddelivery.infrastructure.controller.mapper;

import com.fooddelivery.domain.model.Address;
import com.fooddelivery.domain.model.User;
import com.fooddelivery.domain.model.UserProfile;
import com.fooddelivery.infrastructure.controller.dto.AddressDto;
import com.fooddelivery.infrastructure.controller.dto.UserProfileDto;
import com.fooddelivery.infrastructure.controller.dto.UserProfileResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserProfileResponse toUserProfileResponse(User user) {
        UserProfileDto profileDto = null;
        
        if (user.getProfile() != null) {
            profileDto = toUserProfileDto(user.getProfile());
        }
        
        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().toString(),
                profileDto,
                user.getCreatedAt()
        );
    }

    public UserProfileDto toUserProfileDto(UserProfile profile) {
        List<AddressDto> addressDtos = null;
        
        if (profile.getAddresses() != null) {
            addressDtos = profile.getAddresses().stream()
                    .map(this::toAddressDto)
                    .collect(Collectors.toList());
        }
        
        return new UserProfileDto(
                profile.getName(),
                profile.getPhone(),
                profile.isProfileCompleted(),
                addressDtos
        );
    }

    public AddressDto toAddressDto(Address address) {
        return new AddressDto(
                address.getId(),
                address.getType(),
                address.getStreet(),
                address.getCity(),
                address.getArea(),
                address.isDefault()
        );
    }
}
