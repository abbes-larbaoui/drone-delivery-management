package dz.kyrios.dronedeliverymanagement.domain;

import dz.kyrios.dronedeliverymanagement.statics.DroneStateStatic;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DroneState {
    private Drone drone;
    private LocalDateTime stateTime;
    private DroneStateStatic status;
    //TODO: add Drone location
}
