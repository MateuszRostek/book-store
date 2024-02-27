package book.store.exception;

import java.time.LocalDateTime;

public record ExceptionResponse(String message, LocalDateTime timestamp) {
}
