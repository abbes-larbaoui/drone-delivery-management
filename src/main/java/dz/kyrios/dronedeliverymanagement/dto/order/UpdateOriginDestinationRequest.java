package dz.kyrios.dronedeliverymanagement.dto.order;

import dz.kyrios.dronedeliverymanagement.domain.Location;

public record UpdateOriginDestinationRequest(Location origin, Location destination) {
}
