package dz.kyrios.dronedeliverymanagement.service;

import dz.kyrios.dronedeliverymanagement.configuration.exception.NotFoundException;
import dz.kyrios.dronedeliverymanagement.domain.*;
import dz.kyrios.dronedeliverymanagement.dto.order.OrderResponse;
import dz.kyrios.dronedeliverymanagement.repository.DroneRepositoryMemory;
import dz.kyrios.dronedeliverymanagement.repository.OrderRepositoryMemory;
import dz.kyrios.dronedeliverymanagement.statics.DroneStateStatic;
import dz.kyrios.dronedeliverymanagement.statics.OrderStatusStatic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DroneServiceImplTest {

    @Mock
    private DroneRepositoryMemory droneRepository;

    @Mock
    private OrderRepositoryMemory orderRepository;

    @InjectMocks
    private DroneServiceImpl droneService;

    private Drone availableDrone;
    private Order validOrder;

    @BeforeEach
    void setUp() {
        availableDrone = new Drone();
        availableDrone.setName("drone-1");

        DroneState state = new DroneState();
        state.setState(DroneStateStatic.AVAILABLE);
        availableDrone.setCurrentState(state);

        validOrder = new Order();
        validOrder.setOrderId("order-1");
        validOrder.setStatusHistory(new ArrayList<>());
        validOrder.setCustomer(new Customer("customer-1"));

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setStatus(OrderStatusStatic.CREATED);
        validOrder.setCurrentStatus(orderStatus);
    }

    @Test
    void reserveJob_success() {
        when(droneRepository.findByName("drone-1")).thenReturn(availableDrone);
        when(orderRepository.findById("order-1")).thenReturn(validOrder);
        when(orderRepository.update(any())).thenAnswer(i -> i.getArgument(0));

        OrderResponse response = droneService.reserveJob("order-1", "drone-1");

        assertNotNull(response);
        assertEquals(DroneStateStatic.BUSY, availableDrone.getCurrentState().getState());
        assertEquals(OrderStatusStatic.RESERVED, validOrder.getCurrentStatus().getStatus());

        verify(orderRepository).update(validOrder);
        verify(droneRepository).update(availableDrone);
    }

    @Test
    void reserveJob_droneNotFound() {
        when(droneRepository.findByName("drone-1")).thenReturn(null);

        assertThrows(NotFoundException.class,
                () -> droneService.reserveJob("order-1", "drone-1"));

        verifyNoInteractions(orderRepository);
    }

    @Test
    void reserveJob_droneNotAvailable() {
        availableDrone.getCurrentState().setState(DroneStateStatic.BUSY);
        when(droneRepository.findByName("drone-1")).thenReturn(availableDrone);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> droneService.reserveJob("order-1", "drone-1"));

        assertEquals("Drone is not available", ex.getMessage());
    }

    @Test
    void reserveJob_orderNotFound() {
        when(droneRepository.findByName("drone-1")).thenReturn(availableDrone);
        when(orderRepository.findById("order-1")).thenReturn(null);

        assertThrows(NotFoundException.class,
                () -> droneService.reserveJob("order-1", "drone-1"));
    }

    @Test
    void reserveJob_orderInvalidForReservation() {
        validOrder.getCurrentStatus().setStatus(OrderStatusStatic.RESERVED);
        when(droneRepository.findByName("drone-1")).thenReturn(availableDrone);
        when(orderRepository.findById("order-1")).thenReturn(validOrder);

        assertThrows(RuntimeException.class,
                () -> droneService.reserveJob("order-1", "drone-1"));
    }

    @Test
    void markDroneBroken_withCurrentOrder_createsHandoffOrder() {
        Order currentOrder = new Order();
        currentOrder.setOrderId("order-1");
        currentOrder.setCustomer(new Customer("customer-1"));
        currentOrder.setDescription("Delivery");
        currentOrder.setDestination(new Location());

        Drone drone = new Drone();
        DroneState state = new DroneState();
        state.setState(DroneStateStatic.BUSY);
        drone.setCurrentState(state);
        drone.setCurrentOrder(currentOrder);

        when(droneRepository.findByName("drone-1")).thenReturn(drone);
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = droneService.markDroneBroken("drone-1");

        assertNotNull(response);
        assertEquals(DroneStateStatic.BROKEN, drone.getCurrentState().getState());
        assertNull(drone.getCurrentOrder());

        verify(orderRepository).save(argThat(order ->
                order.getParent() == currentOrder &&
                        order.getCurrentStatus().getStatus() == OrderStatusStatic.HAND_OFF
        ));

        verify(droneRepository).update(drone);
    }

    @Test
    void markDroneBroken_freeDrone_returnsNull() {

        Drone drone = new Drone();
        DroneState state = new DroneState();
        state.setState(DroneStateStatic.BUSY);
        drone.setCurrentState(state);
        drone.setCurrentLocation(new Location());
        drone.setCurrentOrder(null);
        when(droneRepository.findByName("drone-1")).thenReturn(drone);

        OrderResponse response = droneService.markDroneBroken("drone-1");

        assertNull(response);
        assertEquals(DroneStateStatic.BROKEN, drone.getCurrentState().getState());

        verify(orderRepository, never()).save(any());
        verify(droneRepository, never()).update(any());
    }
}