package book.store.dto.cartitem;

import jakarta.validation.constraints.Positive;

public record UpdateCartItemRequestDto(@Positive int quantity) {
}
