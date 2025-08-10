package com.fooddelivery.infrastructure.controller.dto;

import com.fooddelivery.domain.model.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddAddressRequest {
    @NotNull(message = "Address type is required")
    private Address.AddressType type;

    @NotBlank(message = "Street is required")
    private String street;

    @NotBlank(message = "City is required")
    private String city;

    private String area;
    private boolean isDefault;
}
