package com.fooddelivery.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String id;
    private AddressType type;
    private String street;
    private String city;
    private String area;
    private boolean isDefault;
    
    public enum AddressType {
        HOME, WORK, OTHER
    }
}
