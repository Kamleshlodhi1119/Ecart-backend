package com.Ecart.controller;

import com.Ecart.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    private final DashboardService dashboardService;
    

     public AdminDashboardController(DashboardService dashboardService) {
	 	super();
	 	this.dashboardService = dashboardService;
	 }

	@GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        return ResponseEntity.ok(dashboardService.getDashboardSummary());
    }

    @GetMapping("/top-products")
    public ResponseEntity<?> getTopSellingProducts() {
        return ResponseEntity.ok(dashboardService.getTopSellingProducts());
    }

    @GetMapping("/monthly-sales")
    public ResponseEntity<?> getMonthlySales() {
        return ResponseEntity.ok(dashboardService.getMonthlySales());
    }

}
