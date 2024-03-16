package book.store.dto.order;

import book.store.model.Order;
import jakarta.validation.constraints.NotBlank;

public record UpdateStatusRequestDto(@NotBlank Order.Status status) {
}
