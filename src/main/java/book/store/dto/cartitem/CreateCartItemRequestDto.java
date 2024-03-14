package book.store.dto.cartitem;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateCartItemRequestDto(
        @PositiveOrZero
        Long bookId,
        @Positive
        int quantity
) {
}
