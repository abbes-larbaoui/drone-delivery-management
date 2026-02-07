package dz.kyrios.dronedeliverymanagement.dto.order;

import dz.kyrios.dronedeliverymanagement.domain.Location;

public record OrderCreationRequest(Location origin, Location destination, String description) {
}
