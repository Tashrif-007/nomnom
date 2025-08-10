package com.fooddelivery.infrastructure.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericResponse {
    private String message;
    private boolean success = true;
    
    public GenericResponse(String message) {
        this.message = message;
        this.success = true;
    }
}
