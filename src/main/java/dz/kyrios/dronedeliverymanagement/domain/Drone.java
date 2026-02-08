package dz.kyrios.dronedeliverymanagement.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Drone {
    private String name;
    private Location currentLocation;
    private List<Location> locationHistory;
    private Order currentOrder;
    private List<Order> orderHistory;
    private DroneState currentState;
    private List<DroneState> droneStateHistory;

    public void setCurrentOrder(Order currentOrder) {
        this.currentOrder = currentOrder;
        if (this.orderHistory == null) {
            this.orderHistory = new ArrayList<>();
        }
        if (currentOrder != null) {
            this.orderHistory.add(currentOrder);
        }
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
        if (this.locationHistory == null) {
            this.locationHistory = new ArrayList<>();
        }
        this.locationHistory.add(currentLocation);
    }

    public void setCurrentState(DroneState currentState) {
        this.currentState = currentState;
        if (this.droneStateHistory == null) {
            this.droneStateHistory = new ArrayList<>();
        }
        this.droneStateHistory.add(currentState);
    }
}
