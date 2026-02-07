package dz.kyrios.dronedeliverymanagement.dto.order;

import dz.kyrios.dronedeliverymanagement.statics.OrderStatusStatic;

public record OrderCreationResponse(String orderId, OrderStatusStatic orderStatus) {
}
