package dz.kyrios.dronedeliverymanagement.controller;

import dz.kyrios.dronedeliverymanagement.dto.order.OrderCreationRequest;
import dz.kyrios.dronedeliverymanagement.dto.order.OrderCreationResponse;
import dz.kyrios.dronedeliverymanagement.dto.order.OrderResponse;
import dz.kyrios.dronedeliverymanagement.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/api/v1/orders")
    public ResponseEntity<OrderCreationResponse> createOrder(@RequestBody OrderCreationRequest orderCreationRequest,
                                                             Principal principal) {
        OrderCreationResponse response = orderService.createOrder(orderCreationRequest, principal.getName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/api/v1/orders/{order-id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable(name = "order-id") String orderId,
                                                  Principal principal) {
        OrderResponse response = orderService.getOrder(orderId, principal.getName());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/api/v1/orders/{order-id}")
    public ResponseEntity<OrderResponse> withdrawOrder(@PathVariable(name = "order-id") String orderId,
                                                       Principal principal) {
        OrderResponse response = orderService.withdrawOrder(orderId, principal.getName());
        return ResponseEntity.ok(response);
    }
}
