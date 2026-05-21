package roomescape.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Member;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Store;
import roomescape.domain.Theme;
import roomescape.exception.AuthorizationException;
import roomescape.exception.ConflictException;
import roomescape.exception.NotFoundException;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.StoreRepository;
import roomescape.repository.ThemeRepository;

@Service
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final StoreRepository storeRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              ReservationTimeRepository reservationTimeRepository,
                              ThemeRepository themeRepository,
                              StoreRepository storeRepository) {

        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.storeRepository = storeRepository;
    }

    @Transactional
    public Reservation create(Member member, LocalDate date, Long timeId, Long themeId, Long storeId) {
        validateDuplicateReservation(date, timeId, themeId, storeId);
        ReservationTime time = findReservationTime(timeId);
        Theme theme = findTheme(themeId);
        Store store = findStore(storeId);
        Reservation reservation = new Reservation(member, date, time, theme, store, LocalDateTime.now());
        return reservationRepository.insert(reservation, storeId);
    }

    private void validateDuplicateReservation(LocalDate date, Long timeId, Long themeId, Long storeId) {
        if (reservationRepository.existsByStoreIdAndDateAndTimeAndTheme(date, timeId, themeId, storeId)) {
            throw new ConflictException("이미 예약된 시간입니다. 다른 날짜 혹은 테마를 선택해주세요.");
        }
    }

    @Transactional
    public void delete(Member member, Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 예약입니다. 예약을 확인해주세요."));

        validateSameMember(reservation, member, "본인의 예약만 삭제할 수 있습니다.");
        Long cancelId = reservation.getCancelId(LocalDateTime.now());
        reservationRepository.delete(cancelId);
    }

    private ReservationTime findReservationTime(Long timeId) {
        return reservationTimeRepository.findById(timeId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 예약 시간입니다. 시간대를 확인해주세요."));
    }

    private Theme findTheme(Long themeId) {
        return themeRepository.findBy(themeId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 테마입니다. 테마를 확인해주세요."));
    }

    private Store findStore(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 매장입니다."));
    }

    @Transactional
    public Reservation update(Member member, Long id, LocalDate date, Long timeId) {
        Reservation nowReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 예약입니다. 예약을 확인해주세요."));

        validateSameMember(nowReservation, member, "본인의 예약만 수정할 수 있습니다.");
        ReservationTime updateTime = findReservationTime(timeId);
        validateDuplicateReservation(date, timeId, nowReservation.getTheme().getId(), nowReservation.getStore().getId());

        Reservation updateReservation = nowReservation.update(date, updateTime, LocalDateTime.now());
        reservationRepository.updateByDateAndTime(id, date, timeId);
        return updateReservation;
    }

    private void validateSameMember(Reservation reservation, Member member, String message) {
        if (!reservation.isSameMember(member)) {
            throw new AuthorizationException(message);
        }
    }

    public List<Reservation> findByMember(Member member) {
        return reservationRepository.findByMemberId(member.getId());
    }
}
