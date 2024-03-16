package book.store.controller;

import book.store.dto.order.OrderDto;
import book.store.dto.order.PlaceOrderRequestDto;
import book.store.dto.order.UpdateStatusRequestDto;
import book.store.dto.orderitem.OrderItemDto;
import book.store.model.User;
import book.store.service.order.OrderService;
import book.store.service.user.UserService;
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

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public List<OrderDto> getOrdersHistory(Authentication authentication) {
        User user = userService.getUserFromAuthentication(authentication);
        return orderService.getAllOrders(user.getId());
    }

    @GetMapping("/{orderId}/items")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public List<OrderItemDto> getAllItemsFromOrder(@PathVariable Long orderId) {
        return orderService.getAllItemsFromOrder(orderId);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public OrderItemDto getItemFromOrder(@PathVariable Long orderId, @PathVariable Long itemId) {
        return orderService.getItemFromOrder(orderId, itemId);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto placeOrder(
            Authentication authentication,
            @RequestBody @Valid PlaceOrderRequestDto requestDto) {
        User user = userService.getUserFromAuthentication(authentication);
        return orderService.placeOrder(user.getId(), requestDto);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateStatusRequestDto requestDto) {
        orderService.updateStatus(id, requestDto);
    }
}
