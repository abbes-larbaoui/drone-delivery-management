package dz.kyrios.dronedeliverymanagement.service;

import dz.kyrios.dronedeliverymanagement.configuration.exception.NotFoundException;
import dz.kyrios.dronedeliverymanagement.domain.Customer;
import dz.kyrios.dronedeliverymanagement.domain.Location;
import dz.kyrios.dronedeliverymanagement.domain.Order;
import dz.kyrios.dronedeliverymanagement.domain.OrderStatus;
import dz.kyrios.dronedeliverymanagement.dto.order.OrderCreationRequest;
import dz.kyrios.dronedeliverymanagement.dto.order.OrderResponse;
import dz.kyrios.dronedeliverymanagement.mapper.OrderMapper;
import dz.kyrios.dronedeliverymanagement.repository.OrderRepository;
import dz.kyrios.dronedeliverymanagement.statics.OrderStatusStatic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderResponse createOrder(OrderCreationRequest orderCreationRequest, String username) {
        Order order = new Order();
        order.setCustomer(new Customer(username));
        order.setDescription(orderCreationRequest.description());
        order.setOrigin(orderCreationRequest.origin());
        order.setDestination(orderCreationRequest.destination());
        order.setCurrentLocation(orderCreationRequest.destination());

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setStatus(OrderStatusStatic.CREATED);
        orderStatus.setUpdatedAt(LocalDateTime.now());
        orderStatus.setUpdatedBy(username);

        order.setCurrentStatus(orderStatus);

        Order savedOrder = orderRepository.save(order);

        return OrderMapper.orderToOrderResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrder(String orderId, String username) {
        Order order = orderRepository.findByIdAndUsername(orderId, username);

        if (order == null) {
            log.error("Get Order: Order with id {} not found", orderId);
            throw new NotFoundException("Order not found");
        }

        return OrderMapper.orderToOrderResponse(order);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        List<OrderResponse> orderResponses = new ArrayList<>();
        for (Order order : orderRepository.findAll()) {
            OrderResponse orderResponse = OrderMapper.orderToOrderResponse(order);
            orderResponses.add(orderResponse);
        }
        return orderResponses;
    }

    @Override
    public OrderResponse withdrawOrder(String orderId, String username) {
        Order order = orderRepository.findByIdAndUsername(orderId, username);

        if (order == null) {
            log.error("Withdraw Order: Order with id {} not found", orderId);
            throw new NotFoundException("Order not found");
        }
        if (!validForWithdrawal(order)) {
            log.error("Withdraw Order: Order with id {} is not valid", orderId);
            throw new RuntimeException("Can not withdraw order");
        }
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setUpdatedBy(username);
        orderStatus.setUpdatedAt(LocalDateTime.now());
        orderStatus.setStatus(OrderStatusStatic.CANCELLED);

        order.setCurrentStatus(orderStatus);
        order.getStatusHistory().add(orderStatus);

        Order updatedOrder = orderRepository.update(order);

        return OrderMapper.orderToOrderResponse(updatedOrder);
    }

    @Override
    public OrderResponse updateOriginDestination(String orderId, Location origin, Location destination) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            log.error("Update Origin Order: Order with id {} not found", orderId);
            throw new NotFoundException("Order not found");
        }
        order.setOrigin(origin);
        order.setDestination(destination);

        Order updatedOrder = orderRepository.update(order);

        return OrderMapper.orderToOrderResponse(updatedOrder);
    }

    private boolean validForWithdrawal(Order order) {
        return OrderStatusStatic.CREATED.equals(order.getCurrentStatus().getStatus())
                || OrderStatusStatic.RESERVED.equals(order.getCurrentStatus().getStatus());
    }
}
