package book.store.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserRegistrationRequestDto(
        @NotBlank
        @Email
        String email,
        @NotBlank
        @Length(min = 8, max = 25)
        String password,
        @NotBlank
        @Length(min = 8, max = 25)
        String repeatPassword,
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        String shippingAddress) {
}
