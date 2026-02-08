package dz.kyrios.dronedeliverymanagement.controller;


import dz.kyrios.dronedeliverymanagement.configuration.JwtService;
import dz.kyrios.dronedeliverymanagement.dto.authentication.AuthenticationRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final JwtService jwtService;

    public AuthenticationController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/token")
    public Map<String, String> getToken(@RequestBody AuthenticationRequest request) {

        String token = jwtService.generateToken(
                request.name(),
                request.type()
        );

        return Map.of("token", token);
    }
}
