package roomescape.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import roomescape.controller.ValidationMessage;

public record LoginRequest(

        @NotBlank(message = ValidationMessage.EMAIL_IS_BLANK)
        @Size(max = 50, message = ValidationMessage.EMAIL_OVER_MAX_LENGTH)
        String email,

        @NotBlank(message = ValidationMessage.PASSWORD_IS_BLANK)
        @Size(max = 50, message = ValidationMessage.PASSWORD_OVER_MAX_LENGTH)
        String password
) {
}
