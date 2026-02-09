package dz.kyrios.dronedeliverymanagement.service;

import dz.kyrios.dronedeliverymanagement.domain.Location;
import dz.kyrios.dronedeliverymanagement.dto.order.OrderCreationRequest;
import dz.kyrios.dronedeliverymanagement.dto.order.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderCreationRequest orderCreationRequest, String username);
    OrderResponse getOrder(String orderId, String username);
    List<OrderResponse> getAllOrders();
    OrderResponse withdrawOrder(String orderId, String username);
    OrderResponse updateOriginDestination(String orderId, Location origin, Location destination);
}
