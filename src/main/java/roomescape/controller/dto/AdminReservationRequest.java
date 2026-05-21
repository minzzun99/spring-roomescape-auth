package roomescape.controller.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import roomescape.controller.ValidationMessage;

public record AdminReservationRequest(

        @NotNull(message = ValidationMessage.MEMBER_IS_NULL)
        Long memberId,

        @NotNull(message = ValidationMessage.DATE_IS_NULL)
        LocalDate date,

        @NotNull(message = ValidationMessage.TIME_ID_IS_NULL)
        Long timeId,

        @NotNull(message = ValidationMessage.THEME_ID_IS_NULL)
        Long themeId,

        @NotNull(message = ValidationMessage.STORE_ID_IS_NULL)
        Long storeId
) {
}
