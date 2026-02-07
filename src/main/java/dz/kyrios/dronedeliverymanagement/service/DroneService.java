package dz.kyrios.dronedeliverymanagement.service;

import dz.kyrios.dronedeliverymanagement.dto.order.OrderResponse;

public interface DroneService {
    OrderResponse reserveJob(String orderId, String droneName);
}
