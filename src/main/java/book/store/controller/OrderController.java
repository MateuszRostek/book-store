package book.store.controller;

import book.store.dto.order.OrderDto;
import book.store.dto.order.PlaceOrderRequestDto;
import book.store.dto.order.UpdateStatusRequestDto;
import book.store.dto.orderitem.OrderItemDto;
import book.store.model.User;
import book.store.service.order.OrderService;
import book.store.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints for managing orders")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    @Operation(
            summary = "Get orders history",
            description = "Retrieve orders history for the authenticated user."
    )
    @GetMapping
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public List<OrderDto> getOrdersHistory(Authentication authentication) {
        User user = userService.getUserFromAuthentication(authentication);
        return orderService.getAllOrders(user.getId());
    }

    @Operation(
            summary = "Get all items from order",
            description = "Retrieve all items from a specific order for the authenticated user."
    )
    @GetMapping("/{orderId}/items")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public List<OrderItemDto> getAllItemsFromOrder(
            Authentication authentication, @PathVariable Long orderId) {
        User user = userService.getUserFromAuthentication(authentication);
        return orderService.getAllItemsFromOrder(user.getId(), orderId);
    }

    @Operation(
            summary = "Get item from order",
            description = "Retrieve a specific item from a specific order "
                    + "for the authenticated user."
    )
    @GetMapping("/{orderId}/items/{itemId}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public OrderItemDto getItemFromOrder(
            Authentication authentication,
            @PathVariable Long orderId, @PathVariable Long itemId) {
        User user = userService.getUserFromAuthentication(authentication);
        return orderService.getItemFromOrder(user.getId(), orderId, itemId);
    }

    @Operation(
            summary = "Place an order",
            description = "Place a new order for the authenticated user."
    )
    @PostMapping
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto placeOrder(
            Authentication authentication,
            @RequestBody @Valid PlaceOrderRequestDto requestDto) {
        User user = userService.getUserFromAuthentication(authentication);
        return orderService.placeOrder(user, requestDto);
    }

    @Operation(
            summary = "Update order status",
            description = "Update the status of a specific order by its ID."
    )
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateStatusRequestDto requestDto) {
        orderService.updateStatus(id, requestDto);
    }
}
