package book.store.controller;

import book.store.dto.cartitem.CreateCartItemRequestDto;
import book.store.dto.cartitem.UpdateCartItemRequestDto;
import book.store.dto.shoppingcart.ShoppingCartDto;
import book.store.model.User;
import book.store.service.shoppingcart.ShoppingCartService;
import book.store.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService cartService;
    private final UserService userService;

    @GetMapping
    public ShoppingCartDto getCartWithCartItems(Authentication authentication) {
        User user = userService.getUserFromAuthentication(authentication);
        return cartService.getCartWithCartItems(user.getId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCartDto addCartItemToCart(
            Authentication authentication,
            @RequestBody @Valid CreateCartItemRequestDto requestDto) {
        User user = userService.getUserFromAuthentication(authentication);
        return cartService.addCartItemToCart(user.getId(), requestDto);
    }

    @PutMapping("/cart-items/{cartItemId}")
    public ShoppingCartDto updateCartItemQuantityInCart(
            Authentication authentication,
            @RequestBody @Valid UpdateCartItemRequestDto requestDto,
            @PathVariable Long cartItemId) {
        User user = userService.getUserFromAuthentication(authentication);
        return cartService.updateCartItemQuantityInCart(user.getId(), requestDto, cartItemId);
    }

    @DeleteMapping("/cart-items/{cartItemId}")
    public ShoppingCartDto deleteCartItemFromCart(
            Authentication authentication, @PathVariable Long cartItemId) {
        User user = userService.getUserFromAuthentication(authentication);
        return cartService.deleteCartItemFromCart(user.getId(), cartItemId);
    }
}
