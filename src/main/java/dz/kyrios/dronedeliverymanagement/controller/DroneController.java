package dz.kyrios.dronedeliverymanagement.controller;

import dz.kyrios.dronedeliverymanagement.domain.Location;
import dz.kyrios.dronedeliverymanagement.dto.drone.DroneCreationRequest;
import dz.kyrios.dronedeliverymanagement.dto.drone.DroneResponse;
import dz.kyrios.dronedeliverymanagement.dto.order.OrderResponse;
import dz.kyrios.dronedeliverymanagement.service.DroneService;
import dz.kyrios.dronedeliverymanagement.statics.OrderStatusStatic;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

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

    @PreAuthorize("hasRole('DRONE')")
    @PutMapping("/api/v1/drones/jobs/{order-id}/deliver")
    public ResponseEntity<OrderResponse> deliverOrder(@PathVariable("order-id") String orderId,
                                                      Principal principal) {
        OrderResponse response = droneService.deliverOrderOrFailure(orderId, principal.getName(), OrderStatusStatic.DELIVERED);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('DRONE')")
    @PutMapping("/api/v1/drones/jobs/{order-id}/fail")
    public ResponseEntity<OrderResponse> failOrder(@PathVariable("order-id") String orderId,
                                                   Principal principal) {
        OrderResponse response = droneService.deliverOrderOrFailure(orderId, principal.getName(), OrderStatusStatic.FAILED);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('DRONE')")
    @PutMapping("/api/v1/drones/broken")
    public ResponseEntity<OrderResponse> markDroneBroken(Principal principal) {
        OrderResponse response = droneService.markDroneBroken(principal.getName());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('DRONE')")
    @PutMapping("/api/v1/drones/heartbeat")
    public ResponseEntity<DroneResponse> droneHeartbeat(@RequestBody Location location,
                                                        Principal principal) {
        DroneResponse response = droneService.droneHeartbeat(location, principal.getName());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('DRONE')")
    @GetMapping("/api/v1/drones/order")
    public ResponseEntity<OrderResponse> getCurrentOrder(Principal principal) {
        OrderResponse response = droneService.getCurrentOrder(principal.getName());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/v1/drones/admin")
    public ResponseEntity<List<DroneResponse>> getAllDrones() {
        List<DroneResponse> response = droneService.getAllDrones();
        return ResponseEntity.ok(response);
    }

    // return handoff order
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/api/v1/drones/{drone-name}/admin/broken")
    public ResponseEntity<OrderResponse> markDroneBroken(@PathVariable("drone-name") String droneName) {
        OrderResponse response = droneService.markDroneBroken(droneName);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/api/v1/drones/{drone-name}/admin/fix")
    public ResponseEntity<DroneResponse> fixDrone(@PathVariable("drone-name") String droneName) {
        DroneResponse response = droneService.fixDrone(droneName);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/v1/drones/admin")
    public ResponseEntity<DroneResponse> createDrone(@RequestBody DroneCreationRequest request) {
        DroneResponse response = droneService.createDrone(request.droneName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
