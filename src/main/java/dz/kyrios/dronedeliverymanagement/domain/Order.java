package dz.kyrios.dronedeliverymanagement.domain;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Order {
    private String orderId;
    private Customer customer;
    private Location origin;
    private Location destination;
    private Location currentLocation;
    private String description;
    private OrderStatus currentStatus;
    private List<OrderStatus> statusHistory;
    private Order parent;

    public void setCurrentStatus(OrderStatus currentStatus) {
        this.currentStatus = currentStatus;
        if (this.statusHistory == null) {
            this.statusHistory = new ArrayList<>();
        }
        this.statusHistory.add(currentStatus);
    }
}