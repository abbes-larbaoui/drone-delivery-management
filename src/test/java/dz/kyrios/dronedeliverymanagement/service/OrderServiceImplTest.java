package dz.kyrios.dronedeliverymanagement.service;

import dz.kyrios.dronedeliverymanagement.domain.Location;
import dz.kyrios.dronedeliverymanagement.domain.Order;
import dz.kyrios.dronedeliverymanagement.dto.order.OrderCreationRequest;
import dz.kyrios.dronedeliverymanagement.dto.order.OrderResponse;
import dz.kyrios.dronedeliverymanagement.repository.OrderRepositoryMemory;
import dz.kyrios.dronedeliverymanagement.statics.OrderStatusStatic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepositoryMemory orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrder_success() {
        Location origin = new Location();
        Location destination = new Location();

        OrderCreationRequest request = new OrderCreationRequest(
                origin,
                destination,
                "description"
        );

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.createOrder(request, "abbes");

        assertNotNull(response);

        verify(orderRepository).save(argThat(order ->
                order.getCustomer().getName().equals("abbes") &&
                        order.getDescription().equals("description") &&
                        order.getOrigin() == origin &&
                        order.getDestination() == destination &&
                        order.getCurrentLocation() == destination &&
                        order.getCurrentStatus().getStatus() == OrderStatusStatic.CREATED &&
                        order.getCurrentStatus().getUpdatedBy().equals("abbes") &&
                        order.getCurrentStatus().getUpdatedAt() != null
        ));
    }
}
