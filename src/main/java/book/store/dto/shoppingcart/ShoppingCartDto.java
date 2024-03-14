package book.store.dto.shoppingcart;

import book.store.dto.cartitem.CartItemDto;
import java.util.Set;

public record ShoppingCartDto(Long id, Long userId, Set<CartItemDto> cartItems) {
}
