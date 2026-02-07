package dz.kyrios.dronedeliverymanagement.domain;


import lombok.Data;

import java.util.List;

@Data
public class Order {
    private Long orderId;
    private Customer customer;
    private Location origin;
    private Location destination;
    private OrderStatus currentStatus;
    private List<OrderStatus> statusHistory;
}