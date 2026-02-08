package dz.kyrios.dronedeliverymanagement.service;

import dz.kyrios.dronedeliverymanagement.domain.Location;
import dz.kyrios.dronedeliverymanagement.dto.drone.DroneResponse;
import dz.kyrios.dronedeliverymanagement.dto.order.OrderResponse;
import dz.kyrios.dronedeliverymanagement.statics.OrderStatusStatic;

public interface DroneService {
    OrderResponse reserveJob(String orderId, String droneName);
    OrderResponse grabOrder(String orderId, String droneName);
    OrderResponse deliverOrderOrFailure(String orderId, String droneName, OrderStatusStatic status);
    OrderResponse markDroneBroken(String droneName);
    DroneResponse droneHeartbeat(Location location, String droneName);
}
