package dz.kyrios.dronedeliverymanagement.domain;

import lombok.Data;

import java.util.Set;

@Data
public class Customer {
    private String name;
    private Set<Order> orders;
}
