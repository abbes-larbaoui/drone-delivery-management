package dz.kyrios.dronedeliverymanagement.service;

import dz.kyrios.dronedeliverymanagement.configuration.exception.NotFoundException;
import dz.kyrios.dronedeliverymanagement.domain.Order;
import dz.kyrios.dronedeliverymanagement.domain.OrderStatus;
import dz.kyrios.dronedeliverymanagement.dto.order.OrderResponse;
import dz.kyrios.dronedeliverymanagement.repository.OrderRepository;
import dz.kyrios.dronedeliverymanagement.statics.OrderStatusStatic;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DroneServiceImpl implements DroneService {

    private final OrderRepository orderRepository;

    public DroneServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderResponse reserveJob(String orderId, String droneName) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new NotFoundException("Order not found");
        }

        if (!validForReservation(order)) {
            throw new RuntimeException("Order invalid for reservation");
        }

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setStatus(OrderStatusStatic.RESERVED);
        orderStatus.setUpdatedAt(LocalDateTime.now());
        orderStatus.setUpdatedBy(droneName);

        order.setCurrentStatus(orderStatus);
        order.getStatusHistory().add(orderStatus);

        Order updatedOrder = orderRepository.update(order);

        OrderResponse orderResponse = new OrderResponse(
                updatedOrder.getOrderId(),
                updatedOrder.getCustomer().getName(),
                updatedOrder.getCurrentStatus().getStatus().name(),
                updatedOrder.getOrigin(),
                updatedOrder.getDestination(),
                updatedOrder.getDescription()
        );

        return orderResponse;
    }

    @Override
    public OrderResponse grabOrder(String orderId, String droneName) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new NotFoundException("Order not found");
        }

        if (!validForGrab(order)) {
            throw new RuntimeException("Order invalid for pickup");
        }

        if (!order.getCurrentStatus().getUpdatedBy().equals(droneName)) {
            throw new RuntimeException("Order not reserved for you");
        }

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setStatus(OrderStatusStatic.PICKED_UP);
        orderStatus.setUpdatedAt(LocalDateTime.now());
        orderStatus.setUpdatedBy(droneName);

        order.setCurrentStatus(orderStatus);
        order.getStatusHistory().add(orderStatus);

        Order updatedOrder = orderRepository.update(order);

        OrderResponse orderResponse = new OrderResponse(
                updatedOrder.getOrderId(),
                updatedOrder.getCustomer().getName(),
                updatedOrder.getCurrentStatus().getStatus().name(),
                updatedOrder.getOrigin(),
                updatedOrder.getDestination(),
                updatedOrder.getDescription()
        );

        return orderResponse;
    }

    private boolean validForReservation(Order order) {
        return OrderStatusStatic.CREATED.equals(order.getCurrentStatus().getStatus());
    }

    private boolean validForGrab(Order order) {
        return OrderStatusStatic.RESERVED.equals(order.getCurrentStatus().getStatus());
    }
}
