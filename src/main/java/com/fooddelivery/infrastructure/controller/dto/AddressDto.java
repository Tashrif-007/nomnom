package com.fooddelivery.infrastructure.controller.dto;

import com.fooddelivery.domain.model.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    private String id;
    private Address.AddressType type;
    private String street;
    private String city;
    private String area;
    private boolean isDefault;
}
