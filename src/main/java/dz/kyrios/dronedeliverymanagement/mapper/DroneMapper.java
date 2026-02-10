package dz.kyrios.dronedeliverymanagement.mapper;

import dz.kyrios.dronedeliverymanagement.domain.Drone;
import dz.kyrios.dronedeliverymanagement.dto.drone.DroneResponse;

public class DroneMapper {
    public static DroneResponse mapDroneToDroneResponse(Drone drone) {
        String orderId = null;
        String orderStatus = null;

        if (drone.getCurrentOrder() != null) {
            orderId = drone.getCurrentOrder().getOrderId();
            orderStatus = drone.getCurrentOrder()
                    .getCurrentStatus()
                    .getStatus()
                    .name();
        }

        return new DroneResponse(
                drone.getName(),
                drone.getCurrentState().getState().name(),
                drone.getCurrentLocation(),
                orderId,
                orderStatus,
                drone.getDroneStateHistory()
        );
    }
}
