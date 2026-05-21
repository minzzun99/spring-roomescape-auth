package roomescape.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.LoginMember;
import roomescape.controller.dto.AdminReservationRequest;
import roomescape.controller.dto.ReservationResponse;
import roomescape.controller.dto.UpdateReservationRequest;
import roomescape.domain.Member;
import roomescape.domain.Reservation;
import roomescape.service.ReservationService;

@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private final ReservationService reservationService;

    public AdminReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<ReservationResponse> getReservations(@LoginMember Member manager,
                                                     @RequestParam(value = "name", required = false) String name) {
        return reservationService.findAllByManager(name, manager).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@LoginMember Member manager,
                                                                 @Valid @RequestBody AdminReservationRequest request) {

        Reservation reservation = reservationService.createByManager(
                manager,
                request.memberId(),
                request.date(),
                request.timeId(),
                request.themeId(),
                request.storeId());
        return ResponseEntity.created(URI.create("/reservations/" + reservation.getId()))
                .body(ReservationResponse.from(reservation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@LoginMember Member member, @PathVariable Long id) {
        reservationService.deleteByManager(member, id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ReservationResponse> updateReservation(@LoginMember Member member,
                                                                 @PathVariable Long id,
                                                                 @Valid @RequestBody UpdateReservationRequest request) {
        Reservation updateReservation = reservationService.updateByManager(member, id, request.date(), request.timeId());
        return ResponseEntity.ok(ReservationResponse.from(updateReservation));
    }
}
