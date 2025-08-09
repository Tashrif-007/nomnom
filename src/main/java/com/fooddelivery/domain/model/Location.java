package com.fooddelivery.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private String city;
    private String area;
    private Coordinates coordinates;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Coordinates {
        private double lat;
        private double lng;
    }
}
