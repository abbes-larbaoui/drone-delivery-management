package dz.kyrios.dronedeliverymanagement.mapper;

import dz.kyrios.dronedeliverymanagement.domain.Drone;
import dz.kyrios.dronedeliverymanagement.dto.drone.DroneResponse;

public class DroneMapper {
    public static DroneResponse mapDroneToDroneResponse(Drone drone) {
        return new DroneResponse(
                drone.getName(),
                drone.getCurrentState().getState().name(),
                drone.getCurrentLocation(),
                drone.getCurrentOrder().getOrderId(),
                drone.getCurrentOrder().getCurrentStatus().getStatus().name()
        );
    }
}
