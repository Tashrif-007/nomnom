package com.fooddelivery.infrastructure.controller;

import com.fooddelivery.application.service.AnalyticsService;
import com.fooddelivery.infrastructure.controller.dto.AnalyticsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/restaurant")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/{restaurantId}/analytics")
    public ResponseEntity<AnalyticsResponse> getRestaurantAnalytics(
            @PathVariable String restaurantId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to) {
        try {
            LocalDateTime fromDateTime = from != null ? from.atStartOfDay() : null;
            LocalDateTime toDateTime = to != null ? to.atTime(23, 59, 59) : null;

            AnalyticsService.AnalyticsData analytics = analyticsService.getRestaurantAnalytics(
                    restaurantId, fromDateTime, toDateTime);

            AnalyticsResponse response = new AnalyticsResponse(
                    analytics.getTotalOrders(),
                    analytics.getTotalRevenue(),
                    analytics.getFrom().toLocalDate().toString(),
                    analytics.getTo().toLocalDate().toString()
            );

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
