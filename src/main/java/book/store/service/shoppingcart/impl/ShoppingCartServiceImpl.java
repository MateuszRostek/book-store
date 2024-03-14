package book.store.service.shoppingcart.impl;

import book.store.dto.cartitem.CartItemDto;
import book.store.dto.cartitem.CreateCartItemRequestDto;
import book.store.dto.cartitem.UpdateCartItemRequestDto;
import book.store.dto.shoppingcart.ShoppingCartDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.CartItemMapper;
import book.store.mapper.ShoppingCartMapper;
import book.store.model.CartItem;
import book.store.model.ShoppingCart;
import book.store.repository.cartitem.CartItemRepository;
import book.store.repository.shoppingcart.ShoppingCartRepository;
import book.store.service.shoppingcart.ShoppingCartService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartMapper cartMapper;
    private final CartItemMapper cartItemMapper;

    @Override
    public ShoppingCartDto getCartWithCartItems(Long userId) {
        return cartMapper.toDto(cartRepository.findCartWithItems(userId));
    }

    @Override
    public ShoppingCartDto addCartItemToCart(Long userId, CreateCartItemRequestDto requestDto) {
        ShoppingCart modelCart = cartRepository.findCartWithItems(userId);
        CartItem modelCartItem = cartItemMapper.toModel(requestDto);
        Optional<CartItem> existingItem = modelCart.getCartItems().stream()
                .filter(ci -> ci.getBook().getId().equals(modelCartItem.getBook().getId()))
                .findFirst();
        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + modelCartItem.getQuantity());
            cartItemRepository.save(cartItem);
            return cartMapper.toDto(modelCart);
        }
        modelCartItem.setShoppingCart(modelCart);
        modelCart.getCartItems().add(modelCartItem);
        return cartMapper.toDto(cartRepository.save(modelCart));
    }

    @Override
    public CartItemDto updateCartItemQuantityInCart(
            UpdateCartItemRequestDto requestDto, Long cartItemId) {
        CartItem modelCartItem = cartItemRepository.findById(cartItemId).orElseThrow(
                () -> new EntityNotFoundException("Can't find cart item with id: " + cartItemId));
        modelCartItem.setQuantity(requestDto.quantity());
        return cartItemMapper.toDto(cartItemRepository.save(modelCartItem));
    }

    @Override
    public void deleteCartItemFromCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }
}
