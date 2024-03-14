package book.store.service.shoppingcart;

import book.store.dto.cartitem.CreateCartItemRequestDto;
import book.store.dto.cartitem.UpdateCartItemRequestDto;
import book.store.dto.shoppingcart.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto getCartWithCartItems(Long userId);

    ShoppingCartDto addCartItemToCart(Long userId, CreateCartItemRequestDto requestDto);

    ShoppingCartDto updateCartItemQuantityInCart(
            Long userId, UpdateCartItemRequestDto requestDto, Long cartItemId);

    ShoppingCartDto deleteCartItemFromCart(Long userId, Long cartItemId);
}
