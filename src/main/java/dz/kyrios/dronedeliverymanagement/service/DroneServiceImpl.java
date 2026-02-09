package dz.kyrios.dronedeliverymanagement.service;

import dz.kyrios.dronedeliverymanagement.configuration.exception.NotFoundException;
import dz.kyrios.dronedeliverymanagement.domain.*;
import dz.kyrios.dronedeliverymanagement.dto.drone.DroneResponse;
import dz.kyrios.dronedeliverymanagement.dto.order.OrderResponse;
import dz.kyrios.dronedeliverymanagement.mapper.DroneMapper;
import dz.kyrios.dronedeliverymanagement.mapper.OrderMapper;
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
            log.error("Reserve Job: Drone with name {} not found", droneName);
            throw new NotFoundException("Drone not found");
        }

        if (!DroneStateStatic.AVAILABLE.equals(drone.getCurrentState().getState())) {
            log.error("Reserve Job: Drone {} is not AVAILABLE", drone.getName());
            throw new RuntimeException("Drone is not available");
        }

        Order order = orderRepository.findById(orderId);
        if (order == null) {
            log.error("Reserve Job: Order with id {} not found", orderId);
            throw new NotFoundException("Order not found");
        }

        if (!validForReservation(order)) {
            log.error("Reserve Job: Order with id {} is not reserved yet", orderId);
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

        return OrderMapper.orderToOrderResponse(updatedOrder);
    }

    @Override
    public OrderResponse grabOrder(String orderId, String droneName) {
        Drone drone = droneRepository.findByName(droneName);
        if (drone == null) {
            log.error("Grab Order: Drone with name {} not found", droneName);
            throw new NotFoundException("Drone not found");
        }

        Order order = orderRepository.findById(orderId);
        if (order == null) {
            log.error("Grab Order: Order with id {} not found", orderId);
            throw new NotFoundException("Order not found");
        }

        if (!validForGrab(order)) {
            log.error("Grab Order: Order with id {} invalid for pickup", orderId);
            throw new RuntimeException("Order invalid for pickup");
        }

        if (drone.getCurrentOrder() == null || !drone.getCurrentOrder().getOrderId().equals(orderId)) {
            log.error("Grab Order: Order with id {} invalid for drone", orderId);
            throw new RuntimeException("Order not reserved for you");
        }

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setStatus(OrderStatusStatic.PICKED_UP);
        orderStatus.setUpdatedAt(LocalDateTime.now());
        orderStatus.setUpdatedBy(droneName);

        order.setCurrentStatus(orderStatus);
        order.getStatusHistory().add(orderStatus);

        Order updatedOrder = orderRepository.update(order);

        return OrderMapper.orderToOrderResponse(updatedOrder);
    }

    @Override
    public OrderResponse deliverOrderOrFailure(String orderId, String droneName, OrderStatusStatic status) {
        Drone drone = droneRepository.findByName(droneName);
        if (drone == null) {
            log.error("Deliver Order: Drone with name {} not found", droneName);
            throw new NotFoundException("Drone not found");
        }

        Order order = orderRepository.findById(orderId);
        if (order == null) {
            log.error("Deliver Order: Order with id {} not found", orderId);
            throw new NotFoundException("Order not found");
        }

        if (!validForDeliveryOrFailure(order)) {
            log.error("Deliver Order: Order with id {} invalid for delivery", orderId);
            throw new RuntimeException("Order invalid for delivery");
        }

        if (drone.getCurrentOrder() == null || !drone.getCurrentOrder().getOrderId().equals(orderId)) {
            log.error("Deliver Order: Order with id {} invalid for drone", orderId);
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

        return OrderMapper.orderToOrderResponse(updatedOrder);
    }

    @Override
    public OrderResponse markDroneBroken(String droneName) {
        Drone drone = droneRepository.findByName(droneName);
        if (drone == null) {
            log.error("Mark Drone Broken: Drone with name {} not found", droneName);
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

        return OrderMapper.orderToOrderResponse(savedOrder);
    }

    @Override
    public DroneResponse droneHeartbeat(Location location, String droneName) {
        Drone drone = droneRepository.findByName(droneName);
        if (drone == null) {
            log.error("Drone Heartbeat: Drone with name {} not found", droneName);
            throw new NotFoundException("Drone not found");
        }
        drone.setCurrentLocation(location);
        Drone updatedDrone = droneRepository.update(drone);

        Order order = drone.getCurrentOrder();
        if (order != null) {
            order.setCurrentLocation(location);
            orderRepository.update(order);
        }

        return DroneMapper.mapDroneToDroneResponse(updatedDrone);
    }

    @Override
    public OrderResponse getCurrentOrder(String droneName) {
        Drone drone = droneRepository.findByName(droneName);
        if (drone == null) {
            log.error("Get Current Order: Drone with name {} not found", droneName);
            throw new NotFoundException("Drone not found");
        }
        return OrderMapper.orderToOrderResponse(drone.getCurrentOrder());
    }

    @Override
    public List<DroneResponse> getAllDrones() {
        List<DroneResponse> droneResponses = new ArrayList<>();
        for (Drone drone : droneRepository.findAll()) {
            DroneResponse droneResponse = DroneMapper.mapDroneToDroneResponse(drone);
            droneResponses.add(droneResponse);
        }
        return droneResponses;
    }

    @Override
    public DroneResponse fixDrone(String droneName) {
        Drone drone = droneRepository.findByName(droneName);
        if (drone == null) {
            log.error("Fix Drone: Drone with name {} not found", droneName);
            throw new NotFoundException("Drone not found");
        }
        DroneState currentState = new DroneState();
        currentState.setStateTime(LocalDateTime.now());
        currentState.setState(DroneStateStatic.AVAILABLE);

        drone.setCurrentState(currentState);
        Drone updatedDrone = droneRepository.update(drone);

        return DroneMapper.mapDroneToDroneResponse(updatedDrone);
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
