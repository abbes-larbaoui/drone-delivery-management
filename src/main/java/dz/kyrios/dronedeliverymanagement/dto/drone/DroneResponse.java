package dz.kyrios.dronedeliverymanagement.dto.drone;

import dz.kyrios.dronedeliverymanagement.domain.DroneState;
import dz.kyrios.dronedeliverymanagement.domain.Location;

import java.util.List;

public record DroneResponse(String droneName,
                            String droneState,
                            Location location,
                            String currentOrderId,
                            String currentOrderStatus,
                            List<DroneState> stateHistory) {
}
