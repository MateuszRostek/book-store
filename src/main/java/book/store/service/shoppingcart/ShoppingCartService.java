package book.store.service.shoppingcart;

import book.store.dto.cartitem.CartItemDto;
import book.store.dto.cartitem.CreateCartItemRequestDto;
import book.store.dto.cartitem.UpdateCartItemRequestDto;
import book.store.dto.shoppingcart.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto getCartWithItems(Long userId);

    ShoppingCartDto addItemToCart(Long userId, CreateCartItemRequestDto requestDto);

    CartItemDto updateItemQuantity(Long cartItemId, UpdateCartItemRequestDto requestDto);

    void deleteItemFromCart(Long cartItemId);
}
