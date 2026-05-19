package roomescape.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import roomescape.exception.UnprocessableException;

public class Reservation {

    private final Long id;
    private final Member member;
    private final LocalDate date;
    private final ReservationTime time;
    private final Theme theme;

    public Reservation(Long id, Member member, LocalDate date, ReservationTime time, Theme theme) {
        validateDate(date);
        validateTime(time);
        validateTheme(theme);

        this.id = id;
        this.member = member;
        this.date = date;
        this.time = time;
        this.theme = theme;
    }

    public Reservation(Member member, LocalDate date, ReservationTime time, Theme theme, LocalDateTime now) {
        this(null, member, date, time, theme);
        validateCreatable(date, time, now);
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public LocalDate getDate() {
        return date;
    }

    public ReservationTime getTime() {
        return time;
    }

    public Theme getTheme() {
        return theme;
    }

    public Reservation update(LocalDate updateDate, ReservationTime updateTime, LocalDateTime now) {
        LocalDateTime targetDateAndTime = LocalDateTime.of(this.date, this.time.getStartAt());
        validateNotPast(targetDateAndTime, now, "지난 날짜의 예약은 변경할 수 없습니다. 현재 이후의 예약을 선택해주세요.");

        LocalDateTime updateDateAndTime = LocalDateTime.of(updateDate, updateTime.getStartAt());
        validateNotPast(updateDateAndTime, now, "지난 날짜로 예약을 변경할 수 없습니다. 현재 이후의 날짜를 선택해주세요.");

        return new Reservation(this.id, this.member, updateDate, updateTime, this.theme);
    }

    public Long getCancelId(LocalDateTime now) {
        validateCancelable(now);
        return this.id;
    }

    private void validateCancelable(LocalDateTime now) {
        LocalDateTime reservationDateTime = LocalDateTime.of(this.date, this.time.getStartAt());
        if (isPast(reservationDateTime, now)) {
            throw new UnprocessableException("지난 예약은 취소할 수 없습니다. 현재 이후의 예약을 선택해주세요.");
        }
    }

    private void validateCreatable(LocalDate date, ReservationTime time, LocalDateTime now) {
        LocalDateTime reservationDateTime = LocalDateTime.of(date, time.getStartAt());
        if (isPast(reservationDateTime, now)) {
            throw new UnprocessableException("지난 시간으로는 예약할 수 없습니다. 현재 이후의 시간으로 예약해주세요.");
        }
    }

    private boolean isPast(LocalDateTime target, LocalDateTime now) {
        return target.isBefore(now);
    }

    private void validateNotPast(LocalDateTime target, LocalDateTime now, String message) {
        if (isPast(target, now)) {
            throw new UnprocessableException(message);
        }
    }

    private void validateDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("날짜는 비어 있을 수 없습니다. 날짜를 입력해주세요.");
        }
    }

    private void validateTime(ReservationTime time) {
        if (time == null) {
            throw new IllegalArgumentException("예약 시간은 비어있을 수 없습니다. 예약 시간을 선택해주세요.");
        }
    }

    private void validateTheme(Theme theme) {
        if (theme == null) {
            throw new IllegalArgumentException("테마는 비어있을 수 없습니다. 테마를 선택해주세요.");
        }
    }
}
