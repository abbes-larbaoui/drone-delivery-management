package dz.kyrios.dronedeliverymanagement.service;

import dz.kyrios.dronedeliverymanagement.configuration.exception.NotFoundException;
import dz.kyrios.dronedeliverymanagement.domain.Customer;
import dz.kyrios.dronedeliverymanagement.domain.Order;
import dz.kyrios.dronedeliverymanagement.domain.OrderStatus;
import dz.kyrios.dronedeliverymanagement.dto.order.OrderCreationRequest;
import dz.kyrios.dronedeliverymanagement.dto.order.OrderCreationResponse;
import dz.kyrios.dronedeliverymanagement.dto.order.OrderResponse;
import dz.kyrios.dronedeliverymanagement.repository.OrderRepository;
import dz.kyrios.dronedeliverymanagement.statics.OrderStatusStatic;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderCreationResponse createOrder(OrderCreationRequest orderCreationRequest, String username) {
        // 1. order obj
        Order order = new Order();
        order.setCustomer(new Customer(username));
        order.setDescription(orderCreationRequest.description());
        order.setOrigin(orderCreationRequest.origin());
        order.setDestination(orderCreationRequest.destination());
        order.setStatusHistory(new ArrayList<>());

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setStatus(OrderStatusStatic.CREATED);
        orderStatus.setUpdatedAt(LocalDateTime.now());
        orderStatus.setUpdatedBy(username);

        order.setCurrentStatus(orderStatus);
        order.getStatusHistory().add(orderStatus);

        // 2. save the order
        Order savedOrder = orderRepository.save(order);

        // 3. return the response
        return new OrderCreationResponse(savedOrder.getOrderId(), savedOrder.getCurrentStatus().getStatus());
    }

    @Override
    public OrderResponse getOrder(String orderId, String username) {
        Order order = orderRepository.findByIdAndUsername(orderId, username);

        if (order == null) {
            throw new NotFoundException("Order not found");
        }

        OrderResponse orderResponse = new OrderResponse(
                order.getOrderId(),
                order.getCustomer().getName(),
                order.getCurrentStatus().getStatus().name(),
                order.getOrigin(),
                order.getDestination(),
                order.getDescription()
        );
        return orderResponse;
    }
}
