package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.orderitem.OrderItemDto;
import book.store.model.CartItem;
import book.store.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "book.price", target = "price")
    OrderItem toModelFromCartItem(CartItem cartItem);

    @Mapping(source = "book.id", target = "bookId")
    OrderItemDto toDto(OrderItem orderItem);
}
