package book.store.dto.order;

import book.store.dto.orderitem.OrderItemDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record OrderDto(
        Long id,
        Long userId,
        LocalDateTime orderDate,
        BigDecimal total,
        String status,
        Set<OrderItemDto> orderItems) {
}
