package dz.kyrios.dronedeliverymanagement.domain;


import lombok.Data;

import java.util.List;

@Data
public class Order {
    private String orderId;
    private Customer customer;
    private Location origin;
    private Location destination;
    private String description;
    private OrderStatus currentStatus;
    private List<OrderStatus> statusHistory;
}