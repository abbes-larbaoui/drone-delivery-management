package dz.kyrios.dronedeliverymanagement.mapper;

import dz.kyrios.dronedeliverymanagement.domain.Order;
import dz.kyrios.dronedeliverymanagement.dto.order.OrderResponse;

public class OrderMapper {
    public static OrderResponse orderToOrderResponse(Order order) {
        return new OrderResponse(
                order.getOrderId(),
                order.getCustomer().getName(),
                order.getCurrentStatus().getStatus().name(),
                order.getOrigin(),
                order.getDestination(),
                order.getCurrentLocation(),
                order.getDescription()
        );
    }
}
