package dz.kyrios.dronedeliverymanagement.dto.order;

import dz.kyrios.dronedeliverymanagement.domain.Location;

public record OrderResponse(String orderId,
                            String customerName,
                            String currentStatus,
                            Location origin,
                            Location destination,
                            Location currentLocation,
                            String description) {
}
