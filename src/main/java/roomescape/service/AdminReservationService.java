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
import roomescape.repository.MemberRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.StoreManagerRepository;
import roomescape.repository.StoreRepository;
import roomescape.repository.ThemeRepository;

@Service
@Transactional(readOnly = true)
public class AdminReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;
    private final StoreManagerRepository storeManagerRepository;

    public AdminReservationService(ReservationRepository reservationRepository,
                                   ReservationTimeRepository reservationTimeRepository,
                                   ThemeRepository themeRepository,
                                   StoreRepository storeRepository,
                                   MemberRepository memberRepository,
                                   StoreManagerRepository storeManagerRepository) {

        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.storeRepository = storeRepository;
        this.memberRepository = memberRepository;
        this.storeManagerRepository = storeManagerRepository;
    }

    public List<Reservation> findAll(String name, Member manager) {
        Long storeId = storeManagerRepository.findStoreIdByMemberId(manager.getId())
                .orElseThrow(() -> new AuthorizationException("관리하는 매장이 없습니다. 관리자를 확인해주세요."));

        if (name != null) {
            return reservationRepository.findByStoreIdAndName(storeId, name);
        }
        return reservationRepository.findByStoreId(storeId);
    }

    @Transactional
    public Reservation create(Member manager, Long memberId, LocalDate date, Long timeId, Long themeId, Long storeId) {
        validateManagedStore(manager, storeId);
        validateDuplicateReservation(date, timeId, themeId, storeId);

        Member member = findMember(memberId);
        ReservationTime time = findReservationTime(timeId);
        Theme theme = findTheme(themeId);
        Store store = findStore(storeId);

        Reservation reservation = new Reservation(member, date, time, theme, store, LocalDateTime.now());
        return reservationRepository.insert(reservation, storeId);
    }

    @Transactional
    public void delete(Member manager, Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 예약입니다. 예약을 확인해주세요."));
        validateManagedStore(manager, reservation.getStore().getId());

        Long cancelId = reservation.getCancelId(LocalDateTime.now());
        reservationRepository.delete(cancelId);
    }

    @Transactional
    public Reservation update(Member manager, Long id, LocalDate date, Long timeId) {
        Reservation nowReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 예약입니다. 예약을 확인해주세요."));
        validateManagedStore(manager, nowReservation.getStore().getId());

        ReservationTime updateTime = findReservationTime(timeId);
        validateDuplicateReservation(date, timeId, nowReservation.getTheme().getId(), nowReservation.getStore().getId());

        Reservation updateReservation = nowReservation.update(date, updateTime, LocalDateTime.now());
        reservationRepository.updateByDateAndTime(id, date, timeId);
        return updateReservation;
    }

    private void validateManagedStore(Member manager, Long storeId) {
        if (!storeManagerRepository.existsByMemberIdAndStoreId(manager.getId(), storeId)) {
            throw new AuthorizationException("해당하는 매장의 관리자 권한이 필요합니다.");
        }
    }

    private void validateDuplicateReservation(LocalDate date, Long timeId, Long themeId, Long storeId) {
        if (reservationRepository.existsByStoreIdAndDateAndTimeAndTheme(date, timeId, themeId, storeId)) {
            throw new ConflictException("이미 예약된 시간입니다. 다른 날짜 혹은 테마를 선택해주세요.");
        }
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
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
}
