package book.store.service.order;

import book.store.dto.order.OrderDto;
import book.store.dto.order.PlaceOrderRequestDto;
import book.store.dto.order.UpdateStatusRequestDto;
import book.store.dto.orderitem.OrderItemDto;
import book.store.model.User;
import java.util.List;

public interface OrderService {
    List<OrderDto> getAllOrders(Long userId);

    List<OrderItemDto> getAllItemsFromOrder(Long userId, Long orderId);

    OrderItemDto getItemFromOrder(Long userId, Long orderId, Long itemId);

    OrderDto placeOrder(User user, PlaceOrderRequestDto requestDto);

    void updateStatus(Long orderId, UpdateStatusRequestDto requestDto);
}
