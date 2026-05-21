package roomescape.controller;

import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.dto.ReservationTimeRequest;
import roomescape.controller.dto.ReservationTimeResponse;
import roomescape.domain.ReservationTime;
import roomescape.service.ReservationTimeService;

@RestController
@RequestMapping("/admin/times")
public class AdminReservationTimeController {

    private final ReservationTimeService service;

    public AdminReservationTimeController(ReservationTimeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ReservationTimeResponse> createReservationTime(
            @Valid @RequestBody ReservationTimeRequest request) {
        ReservationTime reservationTime = service.create(request.startAt());
        return ResponseEntity.created(URI.create("/times/" + reservationTime.getId()))
                .body(ReservationTimeResponse.from(reservationTime));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationTime(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
