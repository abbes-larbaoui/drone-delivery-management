package dz.kyrios.dronedeliverymanagement.domain;

import dz.kyrios.dronedeliverymanagement.statics.OrderStatusStatic;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderStatus {
    private OrderStatusStatic status;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
