package dz.kyrios.dronedeliverymanagement.repository;

import dz.kyrios.dronedeliverymanagement.domain.Drone;

import java.util.List;

public interface DroneRepository {
    List<Drone> findAll();
    Drone findByName(String droneName);
    Drone update(Drone drone);
    Drone save(Drone drone);
}
