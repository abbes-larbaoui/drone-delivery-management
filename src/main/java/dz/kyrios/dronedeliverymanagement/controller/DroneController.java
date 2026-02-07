package dz.kyrios.dronedeliverymanagement.controller;

import dz.kyrios.dronedeliverymanagement.dto.order.OrderResponse;
import dz.kyrios.dronedeliverymanagement.service.DroneService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class DroneController {

    private final DroneService droneService;

    public DroneController(DroneService droneService) {
        this.droneService = droneService;
    }

    @PreAuthorize("hasRole('DRONE')")
    @PutMapping("/api/v1/drones/jobs/{order-id}/reserve")
    public ResponseEntity<OrderResponse> reserveJob(@PathVariable("order-id") String orderId,
                                                    Principal principal) {
        OrderResponse response = droneService.reserveJob(orderId, principal.getName());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('DRONE')")
    @PutMapping("/api/v1/drones/jobs/{order-id}/grab")
    public ResponseEntity<OrderResponse> grabOrder(@PathVariable("order-id") String orderId,
                                                   Principal principal) {
        OrderResponse response = droneService.grabOrder(orderId, principal.getName());
        return ResponseEntity.ok(response);
    }
}
