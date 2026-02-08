package dz.kyrios.dronedeliverymanagement.dto.drone;

import dz.kyrios.dronedeliverymanagement.domain.Location;

public record DroneResponse(String droneName, Location location, String currentOrderId, String currentOrderStatus) {
}
