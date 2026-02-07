package dz.kyrios.dronedeliverymanagement.service;

import dz.kyrios.dronedeliverymanagement.dto.order.OrderCreationRequest;
import dz.kyrios.dronedeliverymanagement.dto.order.OrderCreationResponse;
import dz.kyrios.dronedeliverymanagement.dto.order.OrderResponse;

public interface OrderService {
    OrderCreationResponse createOrder(OrderCreationRequest orderCreationRequest, String username);
    OrderResponse getOrder(String orderId, String username);
}
