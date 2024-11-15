package jway.gmao.controller;

import jway.gmao.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        Map<String, Object> data = dashboardService.getDashboardData();
        return ResponseEntity.ok(data);
    }

//    @GetMapping("/interventionData")
//    public ResponseEntity<Map<String, Object>> getInterventionData() {
//        Map<String, Object> data = dashboardService.getInterventionData();
//        return ResponseEntity.ok(data);
//    }
}
