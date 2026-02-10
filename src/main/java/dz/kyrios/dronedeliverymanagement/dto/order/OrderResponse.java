package dz.kyrios.dronedeliverymanagement.dto.order;

import dz.kyrios.dronedeliverymanagement.domain.Location;
import dz.kyrios.dronedeliverymanagement.domain.OrderStatus;

import java.util.List;

public record OrderResponse(String orderId,
                            String parentId,
                            String customerName,
                            String currentStatus,
                            Location origin,
                            Location destination,
                            Location currentLocation,
                            String description,
                            List<OrderStatus> statusHistory) {
}
