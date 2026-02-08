package dz.kyrios.dronedeliverymanagement.service;

import dz.kyrios.dronedeliverymanagement.configuration.exception.NotFoundException;
import dz.kyrios.dronedeliverymanagement.domain.*;
import dz.kyrios.dronedeliverymanagement.dto.drone.DroneResponse;
import dz.kyrios.dronedeliverymanagement.dto.order.OrderResponse;
import dz.kyrios.dronedeliverymanagement.repository.DroneRepository;
import dz.kyrios.dronedeliverymanagement.repository.OrderRepository;
import dz.kyrios.dronedeliverymanagement.statics.DroneStateStatic;
import dz.kyrios.dronedeliverymanagement.statics.OrderStatusStatic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DroneServiceImpl implements DroneService {

    private final OrderRepository orderRepository;

    private final DroneRepository droneRepository;

    public DroneServiceImpl(OrderRepository orderRepository,
                            DroneRepository droneRepository) {
        this.orderRepository = orderRepository;
        this.droneRepository = droneRepository;
    }

    @Override
    public OrderResponse reserveJob(String orderId, String droneName) {
        Drone drone = droneRepository.findByName(droneName);
        if (drone == null) {
            throw new NotFoundException("Drone not found");
        }

        if (!DroneStateStatic.AVAILABLE.equals(drone.getCurrentState().getState())) {
            throw new RuntimeException("Drone is not available");
        }

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

        drone.setCurrentOrder(updatedOrder);
        DroneState currentSate = new DroneState();
        currentSate.setState(DroneStateStatic.BUSY);
        currentSate.setStateTime(LocalDateTime.now());
        drone.setCurrentState(currentSate);

        droneRepository.update(drone);

        OrderResponse orderResponse = new OrderResponse(
                updatedOrder.getOrderId(),
                updatedOrder.getCustomer().getName(),
                updatedOrder.getCurrentStatus().getStatus().name(),
                updatedOrder.getOrigin(),
                updatedOrder.getDestination(),
                updatedOrder.getCurrentLocation(),
                updatedOrder.getDescription()
        );

        return orderResponse;
    }

    @Override
    public OrderResponse grabOrder(String orderId, String droneName) {
        Drone drone = droneRepository.findByName(droneName);
        if (drone == null) {
            throw new NotFoundException("Drone not found");
        }

        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new NotFoundException("Order not found");
        }

        if (!validForGrab(order)) {
            throw new RuntimeException("Order invalid for pickup");
        }

        if (drone.getCurrentOrder() == null || !drone.getCurrentOrder().getOrderId().equals(orderId)) {
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
                updatedOrder.getCurrentLocation(),
                updatedOrder.getDescription()
        );

        return orderResponse;
    }

    @Override
    public OrderResponse deliverOrderOrFailure(String orderId, String droneName, OrderStatusStatic status) {
        Drone drone = droneRepository.findByName(droneName);
        if (drone == null) {
            throw new NotFoundException("Drone not found");
        }

        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new NotFoundException("Order not found");
        }

        if (!validForDeliveryOrFailure(order)) {
            throw new RuntimeException("Order invalid for delivery");
        }

        if (drone.getCurrentOrder() == null || !drone.getCurrentOrder().getOrderId().equals(orderId)) {
            throw new RuntimeException("Order not picked by for you");
        }

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setStatus(status);
        orderStatus.setUpdatedAt(LocalDateTime.now());
        orderStatus.setUpdatedBy(droneName);

        order.setCurrentStatus(orderStatus);
        order.getStatusHistory().add(orderStatus);
        if (OrderStatusStatic.DELIVERED.equals(orderStatus.getStatus())) {
            order.setCurrentLocation(order.getDestination());
        } else if (OrderStatusStatic.FAILED.equals(orderStatus.getStatus())) {
            order.setCurrentLocation(order.getOrigin());
        }

        Order updatedOrder = orderRepository.update(order);
        drone.setCurrentOrder(null);
        DroneState currentSate = new DroneState();
        currentSate.setState(DroneStateStatic.AVAILABLE);
        currentSate.setStateTime(LocalDateTime.now());
        drone.setCurrentState(currentSate);

        droneRepository.update(drone);

        OrderResponse orderResponse = new OrderResponse(
                updatedOrder.getOrderId(),
                updatedOrder.getCustomer().getName(),
                updatedOrder.getCurrentStatus().getStatus().name(),
                updatedOrder.getOrigin(),
                updatedOrder.getDestination(),
                updatedOrder.getCurrentLocation(),
                updatedOrder.getDescription()
        );

        return orderResponse;
    }

    @Override
    public OrderResponse markDroneBroken(String droneName) {
        Drone drone = droneRepository.findByName(droneName);
        if (drone == null) {
            throw new NotFoundException("Drone not found");
        }

        DroneState currentState = new DroneState();
        currentState.setState(DroneStateStatic.BROKEN);
        currentState.setStateTime(LocalDateTime.now());
        drone.setCurrentState(currentState);

        if (drone.getCurrentOrder() == null) {
            log.warn("Free drone {} is broken in {}, {}",
                    droneName,
                    drone.getCurrentLocation().getLatitude(),
                    drone.getCurrentLocation().getLongitude());
            return null;
        }

        Order handoffOrder = new Order();
        handoffOrder.setParent(drone.getCurrentOrder());
        handoffOrder.setCustomer(drone.getCurrentOrder().getCustomer());
        handoffOrder.setDescription(drone.getCurrentOrder().getDescription());
        handoffOrder.setOrigin(drone.getCurrentLocation());
        handoffOrder.setDestination(drone.getCurrentOrder().getDestination());

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setStatus(OrderStatusStatic.HAND_OFF);
        orderStatus.setUpdatedAt(LocalDateTime.now());
        orderStatus.setUpdatedBy(drone.getName());

        handoffOrder.setCurrentStatus(orderStatus);

        Order savedOrder = orderRepository.save(handoffOrder);

        drone.setCurrentOrder(null);
        droneRepository.update(drone);

        OrderResponse orderResponse = new OrderResponse(
                savedOrder.getOrderId(),
                savedOrder.getCustomer().getName(),
                savedOrder.getCurrentStatus().getStatus().name(),
                savedOrder.getOrigin(),
                savedOrder.getDestination(),
                savedOrder.getCurrentLocation(),
                savedOrder.getDescription()
        );

        return orderResponse;
    }

    @Override
    public DroneResponse droneHeartbeat(Location location, String droneName) {
        Drone drone = droneRepository.findByName(droneName);
        if (drone == null) {
            throw new NotFoundException("Drone not found");
        }
        drone.setCurrentLocation(location);
        Drone updatedDrone = droneRepository.update(drone);

        Order order = drone.getCurrentOrder();
        if (order != null) {
            order.setCurrentLocation(location);
            orderRepository.update(order);
        }

        DroneResponse droneResponse = new DroneResponse(
                updatedDrone.getName(),
                updatedDrone.getCurrentLocation(),
                updatedDrone.getCurrentOrder().getOrderId(),
                updatedDrone.getCurrentOrder().getCurrentStatus().getStatus().name()
        );
        return droneResponse;
    }

    @Override
    public OrderResponse getCurrentOrder(String droneName) {
        Drone drone = droneRepository.findByName(droneName);
        if (drone == null) {
            throw new NotFoundException("Drone not found");
        }
        OrderResponse orderResponse = new OrderResponse(
                drone.getCurrentOrder().getOrderId(),
                drone.getCurrentOrder().getCustomer().getName(),
                drone.getCurrentOrder().getCurrentStatus().getStatus().name(),
                drone.getCurrentOrder().getOrigin(),
                drone.getCurrentOrder().getDestination(),
                drone.getCurrentOrder().getCurrentLocation(),
                drone.getCurrentOrder().getDescription()
        );

        return orderResponse;
    }

    @Override
    public List<DroneResponse> getAllDrones() {
        List<DroneResponse> droneResponses = new ArrayList<>();
        for (Drone drone : droneRepository.findAll()) {
            DroneResponse droneResponse = new DroneResponse(
                    drone.getName(),
                    drone.getCurrentLocation(),
                    drone.getCurrentOrder().getOrderId(),
                    drone.getCurrentOrder().getCurrentStatus().getStatus().name()
            );
            droneResponses.add(droneResponse);
        }
        return droneResponses;
    }

    @Override
    public DroneResponse fixDrone(String droneName) {
        Drone drone = droneRepository.findByName(droneName);
        if (drone == null) {
            throw new NotFoundException("Drone not found");
        }
        DroneState currentState = new DroneState();
        currentState.setStateTime(LocalDateTime.now());
        currentState.setState(DroneStateStatic.AVAILABLE);

        drone.setCurrentState(currentState);
        Drone updatedDrone = droneRepository.update(drone);

        DroneResponse droneResponse = new DroneResponse(
                updatedDrone.getName(),
                updatedDrone.getCurrentLocation(),
                updatedDrone.getCurrentOrder().getOrderId(),
                updatedDrone.getCurrentOrder().getCurrentStatus().getStatus().name()
        );
        return droneResponse;
    }

    private boolean validForReservation(Order order) {
        return OrderStatusStatic.CREATED.equals(order.getCurrentStatus().getStatus());
    }

    private boolean validForGrab(Order order) {
        return OrderStatusStatic.RESERVED.equals(order.getCurrentStatus().getStatus())
                || OrderStatusStatic.HAND_OFF.equals(order.getCurrentStatus().getStatus());
    }

    private boolean validForDeliveryOrFailure(Order order) {
        return OrderStatusStatic.PICKED_UP.equals(order.getCurrentStatus().getStatus());
    }
}
