package book.store.service.order.impl;

import book.store.dto.order.OrderDto;
import book.store.dto.order.PlaceOrderRequestDto;
import book.store.dto.order.UpdateStatusRequestDto;
import book.store.dto.orderitem.OrderItemDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.OrderItemMapper;
import book.store.mapper.OrderMapper;
import book.store.model.*;
import book.store.repository.cartitem.CartItemRepository;
import book.store.repository.order.OrderRepository;
import book.store.repository.shoppingcart.ShoppingCartRepository;
import book.store.service.order.OrderService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ShoppingCartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    public List<OrderDto> getAllOrders(Long userId) {
        return orderRepository.findAllByUserId(userId).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public List<OrderItemDto> getAllItemsFromOrder(Long userId, Long orderId) {
        Order modelOrder = orderRepository.findByIdWithItems(orderId).orElseThrow(
                () -> new EntityNotFoundException("Can't find order with id: " + orderId));
        if (!modelOrder.getUser().getId().equals(userId)) {
            return null;
        }
        return modelOrder.getOrderItems().stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public OrderItemDto getItemFromOrder(Long userId, Long orderId, Long itemId) {
        Order modelOrder = orderRepository.findByIdWithItems(orderId).orElseThrow(
                () -> new EntityNotFoundException("Can't find order with id: " + orderId));
        if (!modelOrder.getUser().getId().equals(userId)) {
            return null;
        }
        OrderItem modelItem = modelOrder.getOrderItems().stream()
                .filter(oi -> oi.getId().equals(itemId))
                .findFirst()
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find item with id: " + itemId));
        return orderItemMapper.toDto(modelItem);
    }

    @Transactional
    @Override
    public OrderDto placeOrder(User user, PlaceOrderRequestDto requestDto) {
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(requestDto.shippingAddress());
        ShoppingCart modelCart = cartRepository.findCartWithItemsByUserId(user.getId());
        Set<OrderItem> orderItems = modelCart.getCartItems().stream()
                .map(orderItemMapper::toModelFromCartItem)
                .collect(Collectors.toSet());
        orderItems.forEach(oi -> oi.setOrder(order));
        order.setOrderItems(orderItems);

        BigDecimal total = modelCart.getCartItems().stream()
                .map(ci -> ci.getBook().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotal(total);
        cartItemRepository.deleteAllByCartId(modelCart.getId());
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public void updateStatus(Long orderId, UpdateStatusRequestDto requestDto) {
        Order modelOrder = orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException("Can't find order with id: " + orderId));
        modelOrder.setStatus(Order.Status.valueOf(requestDto.status()));
        orderRepository.save(modelOrder);
    }
}
