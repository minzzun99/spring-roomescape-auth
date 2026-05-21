package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import roomescape.domain.Member;
import roomescape.domain.Reservation;
import roomescape.exception.ConflictException;
import roomescape.exception.NotFoundException;
import roomescape.repository.MemberRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.StoreRepository;
import roomescape.repository.ThemeRepository;

@JdbcTest
class ReservationServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private ReservationService reservationService;

    private final LocalDate date = LocalDate.parse("2099-08-05");
    private Member brown;
    private Member jerry;

    @BeforeEach
    void setup() {
        jdbcTemplate.update("DELETE FROM reservation;");
        jdbcTemplate.update("ALTER TABLE reservation ALTER COLUMN id RESTART WITH 1;");

        ReservationRepository reservationRepository = new ReservationRepository(jdbcTemplate);
        ReservationTimeRepository reservationTimeRepository = new ReservationTimeRepository(jdbcTemplate);
        ThemeRepository themeRepository = new ThemeRepository(jdbcTemplate);
        StoreRepository storeRepository = new StoreRepository(jdbcTemplate);
        MemberRepository memberRepository = new MemberRepository(jdbcTemplate);
        this.reservationService = new ReservationService(reservationRepository, reservationTimeRepository,
                themeRepository, storeRepository);
        this.brown = memberRepository.findByEmail("brown@email.com").orElseThrow();
        this.jerry = memberRepository.findByEmail("jerry@email.com").orElseThrow();
    }

    @Test
    void 예약_생성_테스트() {
        // when
        Reservation result = reservationService.create(brown, date, 1L, 1L, 1L);

        // then
        assertAll(
                () -> assertThat(result.getId()).isNotNull(),
                () -> assertThat(result.getMember().getName()).isEqualTo("브라운"),
                () -> assertThat(result.getDate()).isEqualTo(date)
        );
    }

    @Test
    void 사용자_예약_조회_테스트() {
        // given
        reservationService.create(brown, date, 1L, 1L, 1L);
        reservationService.create(jerry, date, 2L, 1L, 1L);

        // when
        List<Reservation> result = reservationService.findByMember(brown);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    void 사용자_본인의_예약만_조회_테스트() {
        // given
        reservationService.create(brown, date, 1L, 1L, 1L);
        reservationService.create(brown, date, 2L, 1L, 1L);
        reservationService.create(jerry, date, 3L, 1L, 1L);

        // when
        List<Reservation> result = reservationService.findByMember(brown);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(reservation -> reservation.isSameMember(brown));
    }

    @Test
    void 예약_삭제_테스트() {
        // given
        Reservation created = reservationService.create(brown, date, 1L, 1L, 1L);

        // when
        reservationService.delete(brown, created.getId());

        // then
        assertThat(reservationService.findByMember(brown)).isEmpty();
    }

    @Test
    void 존재하지않는_timeId로_예약_생성_시_예외_발생() {
        // when & then
        assertThatThrownBy(() -> reservationService.create(brown, date, 999L, 1L, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 예약 시간입니다. 시간대를 확인해주세요.");
    }

    @Test
    void 존재하지않는_themeId로_예약_생성_시_예외_발생() {
        // when & then
        assertThatThrownBy(() -> reservationService.create(brown, date, 1L, 999L, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 테마입니다. 테마를 확인해주세요.");
    }

    @Test
    void 존재하지않는_id의_예약_삭제_시_예외_발생() {
        // when & then
        assertThatThrownBy(() -> reservationService.delete(brown, 999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 예약입니다. 예약을 확인해주세요.");
    }

    @Test
    void 중복_예약_시_예외_발생() {
        // given
        reservationService.create(brown, date, 1L, 1L, 1L);

        // when & then
        assertThatThrownBy(() -> reservationService.create(brown, date, 1L, 1L, 1L))
                .isInstanceOf(ConflictException.class)
                .hasMessage("이미 예약된 시간입니다. 다른 날짜 혹은 테마를 선택해주세요.");
    }
}
