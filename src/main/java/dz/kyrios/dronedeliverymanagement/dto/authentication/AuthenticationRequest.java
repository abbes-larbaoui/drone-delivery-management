package dz.kyrios.dronedeliverymanagement.dto.authentication;

import dz.kyrios.dronedeliverymanagement.statics.UserRole;

public record AuthenticationRequest(String name, UserRole type) {
}
