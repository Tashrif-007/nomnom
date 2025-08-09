package com.fooddelivery.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    private String name;
    private String phone;
    private boolean profileCompleted;
    private List<Address> addresses;
}
