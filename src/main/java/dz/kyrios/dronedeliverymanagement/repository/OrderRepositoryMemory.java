package dz.kyrios.dronedeliverymanagement.repository;

import dz.kyrios.dronedeliverymanagement.domain.Order;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderRepositoryMemory implements OrderRepository {

    private final Map<String, Order> orders;

    OrderRepositoryMemory() {
        orders = new HashMap<>();
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    @Override
    public Order findByIdAndUsername(String orderId, String username) {
        Order order = orders.get(orderId);
        if (order == null || !order.getCustomer().getName().equals(username)) {
            return null;
        }
        return order;
    }

    @Override
    public Order save(Order order) {
        String orderId = UUID.randomUUID().toString();
        order.setOrderId(orderId);
        orders.put(orderId, order);
        return orders.get(orderId);
    }

    @Override
    public void delete(String orderId) {
        orders.remove(orderId);
    }

    @Override
    public Order update(Order order) {
        String orderId = order.getOrderId();
        orders.put(orderId, order);
        return orders.get(orderId);
    }
}
