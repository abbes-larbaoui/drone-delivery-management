package dz.kyrios.dronedeliverymanagement.repository;

import dz.kyrios.dronedeliverymanagement.domain.Drone;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DroneRepositoryMemory implements DroneRepository {

    private final Map<String, Drone> drones;

    public DroneRepositoryMemory() {
        this.drones = new HashMap<>();
    }

    @Override
    public List<Drone> findAll() {
        return new ArrayList<>(drones.values());
    }

    @Override
    public Drone findByName(String droneName) {
        return drones.get(droneName);
    }

    @Override
    public Drone update(Drone drone) {
        String droneName = drone.getName();
        drones.put(droneName, drone);
        return drones.get(droneName);
    }
}
