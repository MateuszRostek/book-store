package book.store.service.shoppingcart.impl;

import book.store.dto.cartitem.CreateCartItemRequestDto;
import book.store.dto.cartitem.UpdateCartItemRequestDto;
import book.store.dto.shoppingcart.ShoppingCartDto;
import book.store.mapper.CartItemMapper;
import book.store.mapper.ShoppingCartMapper;
import book.store.model.CartItem;
import book.store.model.ShoppingCart;
import book.store.repository.shoppingcart.ShoppingCartRepository;
import book.store.service.shoppingcart.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository cartRepository;
    private final ShoppingCartMapper cartMapper;
    private final CartItemMapper cartItemMapper;

    @Override
    public ShoppingCartDto getCartWithCartItems(Long userId) {
        return cartMapper.toDto(cartRepository.findCartWithItems(userId));
    }

    @Override
    public ShoppingCartDto addCartItemToCart(Long userId, CreateCartItemRequestDto requestDto) {
        ShoppingCart modelCart = cartRepository.findCartWithItems(userId);
        CartItem modelItem = cartItemMapper.toModel(requestDto);
        modelItem.setShoppingCart(modelCart);
        modelCart.getCartItems().add(modelItem);
        return cartMapper.toDto(cartRepository.save(modelCart));
    }

    @Override
    public ShoppingCartDto updateCartItemQuantityInCart(
            Long userId, UpdateCartItemRequestDto requestDto, Long cartItemId) {
        return null;
    }

    @Override
    public ShoppingCartDto deleteCartItemFromCart(Long userId, Long id) {
        return null;
    }
}
