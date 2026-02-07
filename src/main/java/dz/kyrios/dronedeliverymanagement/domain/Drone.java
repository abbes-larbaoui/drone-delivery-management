package dz.kyrios.dronedeliverymanagement.domain;

import lombok.Data;

import java.util.List;

@Data
public class Drone {
    private String name;
    private Location currentLocation;
    private List<Location> locationHistory;
    private Order currentOrder;
    private List<Order> orderHistory;
    private DroneState currentState;
}
