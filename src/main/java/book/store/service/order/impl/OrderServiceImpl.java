package book.store.service.order.impl;

import book.store.dto.order.OrderDto;
import book.store.dto.order.PlaceOrderRequestDto;
import book.store.dto.order.UpdateStatusRequestDto;
import book.store.dto.orderitem.OrderItemDto;
import book.store.mapper.OrderItemMapper;
import book.store.mapper.OrderMapper;
import book.store.repository.order.OrderRepository;
import book.store.repository.orderitem.OrderItemRepository;
import book.store.service.order.OrderService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
    private OrderRepository orderRepository;
    private OrderItemRepository itemRepository;
    private OrderMapper orderMapper;
    private OrderItemMapper itemMapper;

    @Override
    public List<OrderDto> getAllOrders(Long userId) {
        return null;
    }

    @Override
    public List<OrderItemDto> getAllItemsFromOrder(Long orderId) {
        return null;
    }

    @Override
    public OrderItemDto getItemFromOrder(Long orderId, Long itemId) {
        return null;
    }

    @Override
    public OrderDto placeOrder(Long userId, PlaceOrderRequestDto requestDto) {
        return null;
    }

    @Override
    public void updateStatus(Long orderId, UpdateStatusRequestDto requestDto) {

    }
}
