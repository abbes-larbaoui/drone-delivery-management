package dz.kyrios.dronedeliverymanagement.domain;

import dz.kyrios.dronedeliverymanagement.statics.OrderStatusStatic;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderStatus {
    private Order order;
    private LocalDateTime statusTime;
    private OrderStatusStatic status;
}
