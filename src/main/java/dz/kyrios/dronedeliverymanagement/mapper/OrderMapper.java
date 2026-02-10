package dz.kyrios.dronedeliverymanagement.mapper;

import dz.kyrios.dronedeliverymanagement.domain.Order;
import dz.kyrios.dronedeliverymanagement.dto.order.OrderResponse;

public class OrderMapper {
    public static OrderResponse orderToOrderResponse(Order order) {
        String parentId = null;
        if (order.getParent() != null) {
            parentId = order.getParent().getOrderId();
        }

        return new OrderResponse(
                order.getOrderId(),
                parentId,
                order.getCustomer().getName(),
                order.getCurrentStatus().getStatus().name(),
                order.getOrigin(),
                order.getDestination(),
                order.getCurrentLocation(),
                order.getDescription(),
                order.getStatusHistory()
        );
    }
}
