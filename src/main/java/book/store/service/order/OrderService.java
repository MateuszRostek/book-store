package book.store.service.order;

import book.store.dto.order.OrderDto;
import book.store.dto.order.PlaceOrderRequestDto;
import book.store.dto.order.UpdateStatusRequestDto;
import book.store.dto.orderitem.OrderItemDto;
import java.util.List;

public interface OrderService {
    List<OrderDto> getAllOrders(Long userId);

    List<OrderItemDto> getAllItemsFromOrder(Long orderId);

    OrderItemDto getItemFromOrder(Long orderId, Long itemId);

    OrderDto placeOrder(Long userId, PlaceOrderRequestDto requestDto);

    void updateStatus(Long orderId, UpdateStatusRequestDto requestDto);
}
