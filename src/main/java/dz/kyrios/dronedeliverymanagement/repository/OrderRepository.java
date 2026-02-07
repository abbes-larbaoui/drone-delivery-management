package dz.kyrios.dronedeliverymanagement.repository;

import dz.kyrios.dronedeliverymanagement.domain.Order;

import java.util.List;

public interface OrderRepository {
    List<Order> findAll();
    Order findByIdAndUsername(String orderId, String username);
    Order save(Order order);
    void delete(String orderId);
    Order update(Order order);
}
