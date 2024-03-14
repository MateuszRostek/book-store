package book.store.controller;

import book.store.dto.cartitem.CartItemDto;
import book.store.dto.cartitem.CreateCartItemRequestDto;
import book.store.dto.cartitem.UpdateCartItemRequestDto;
import book.store.dto.shoppingcart.ShoppingCartDto;
import book.store.model.User;
import book.store.service.shoppingcart.ShoppingCartService;
import book.store.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

@Tag(name = "Shopping Cart management", description = "Endpoints for managing shopping carts")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService cartService;
    private final UserService userService;

    @Operation(
            summary = "Retrieve user's shopping cart with cart items",
            description = "Get the shopping cart of the authenticated user"
                    + " with its associated cart items.")
    @GetMapping
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ShoppingCartDto getCartWithCartItems(Authentication authentication) {
        User user = userService.getUserFromAuthentication(authentication);
        return cartService.getCartWithCartItems(user.getId());
    }

    @Operation(
            summary = "Add a new cart item to the shopping cart",
            description = "Add a new cart item to the shopping cart of the authenticated user."
    )
    @PostMapping
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCartDto addCartItemToCart(
            Authentication authentication,
            @RequestBody @Valid CreateCartItemRequestDto requestDto) {
        User user = userService.getUserFromAuthentication(authentication);
        return cartService.addCartItemToCart(user.getId(), requestDto);
    }

    @Operation(
            summary = "Update cart item quantity in the shopping cart",
            description = "Update the quantity of an existing item "
                    + "in the user's shopping cart"
    )
    @PutMapping("/cart-items/{cartItemId}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public CartItemDto updateCartItemQuantityInCart(
            @RequestBody @Valid UpdateCartItemRequestDto requestDto,
            @PathVariable Long cartItemId) {
        return cartService.updateCartItemQuantityInCart(requestDto, cartItemId);
    }

    @Operation(
            summary = "Delete a cart item from the shopping cart",
            description = "Remove an item from the user's shopping cart."
    )
    @DeleteMapping("/cart-items/{cartItemId}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCartItemFromCart(@PathVariable Long cartItemId) {
        cartService.deleteCartItemFromCart(cartItemId);
    }
}
