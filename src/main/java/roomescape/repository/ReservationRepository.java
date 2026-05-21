package roomescape.repository;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.domain.Member;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Role;
import roomescape.domain.Store;
import roomescape.domain.Theme;

@Repository
public class ReservationRepository {
    private static final Long DEFAULT_STORE_ID = 1L;

    private final JdbcTemplate jdbcTemplate;

    public ReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Reservation> findAll() {
        String sql = "SELECT\n" +
                "    r.id as reservation_id,\n" +
                "    m.id as member_id,\n" +
                "    m.name as member_name,\n" +
                "    m.email,\n" +
                "    m.password,\n" +
                "    m.role as member_role,\n" +
                "    r.store_id,\n" +
                "    s.name as store_name,\n" +
                "    r.date,\n" +
                "    rt.id as time_id,\n" +
                "    rt.start_at as time_value,\n" +
                "    t.id as theme_id,\n" +
                "    t.name as theme_name,\n" +
                "    t.description,\n" +
                "    t.thumbnail\n" +
                "FROM reservation as r\n" +
                "INNER JOIN member as m\n" +
                "  ON r.member_id = m.id\n" +
                "INNER JOIN store as s\n" +
                "  ON r.store_id = s.id\n" +
                "INNER JOIN reservation_time as rt\n" +
                "  ON r.time_id = rt.id\n" +
                "INNER JOIN theme as t\n" +
                "  ON r.theme_id = t.id\n";

        return jdbcTemplate.query(sql, reservationRowMapper);
    }

    public List<Reservation> findByName(String name) {
        String sql = "SELECT\n" +
                "    r.id as reservation_id,\n" +
                "    m.id as member_id,\n" +
                "    m.name as member_name,\n" +
                "    m.email,\n" +
                "    m.password,\n" +
                "    m.role as member_role,\n" +
                "    r.store_id,\n" +
                "    s.name as store_name,\n" +
                "    r.date,\n" +
                "    rt.id as time_id,\n" +
                "    rt.start_at as time_value,\n" +
                "    t.id as theme_id,\n" +
                "    t.name as theme_name,\n" +
                "    t.description,\n" +
                "    t.thumbnail\n" +
                "FROM reservation as r\n" +
                "INNER JOIN member as m\n" +
                "  ON r.member_id = m.id\n" +
                "INNER JOIN store as s\n" +
                "  ON r.store_id = s.id\n" +
                "INNER JOIN reservation_time as rt\n" +
                "  ON r.time_id = rt.id\n" +
                "INNER JOIN theme as t\n" +
                "  ON r.theme_id = t.id\n" +
                "WHERE m.name = ?\n";

        return jdbcTemplate.query(sql, reservationRowMapper, name);
    }

    public List<Reservation> findByMemberId(Long memberId) {
        String sql = "SELECT\n" +
                "    r.id as reservation_id,\n" +
                "    m.id as member_id,\n" +
                "    m.name as member_name,\n" +
                "    m.email,\n" +
                "    m.password,\n" +
                "    m.role as member_role,\n" +
                "    r.store_id,\n" +
                "    s.name as store_name,\n" +
                "    r.date,\n" +
                "    rt.id as time_id,\n" +
                "    rt.start_at as time_value,\n" +
                "    t.id as theme_id,\n" +
                "    t.name as theme_name,\n" +
                "    t.description,\n" +
                "    t.thumbnail\n" +
                "FROM reservation as r\n" +
                "INNER JOIN member as m\n" +
                "  ON r.member_id = m.id\n" +
                "INNER JOIN store as s\n" +
                "  ON r.store_id = s.id\n" +
                "INNER JOIN reservation_time as rt\n" +
                "  ON r.time_id = rt.id\n" +
                "INNER JOIN theme as t\n" +
                "  ON r.theme_id = t.id\n" +
                "WHERE m.id = ?\n";

        return jdbcTemplate.query(sql, reservationRowMapper, memberId);
    }

    public Reservation insert(Reservation reservation, Long storeId) {
        String sql = "INSERT INTO reservation(member_id, store_id, date, time_id, theme_id) VALUES (?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(
                    sql,
                    new String[]{"id"});
            pstmt.setLong(1, reservation.getMember().getId());
            pstmt.setLong(2, storeId);
            pstmt.setObject(3, reservation.getDate());
            pstmt.setLong(4, reservation.getTime().getId());
            pstmt.setLong(5, reservation.getTheme().getId());
            return pstmt;
        }, keyHolder);

        return new Reservation(keyHolder.getKey().longValue(), reservation.getMember(), reservation.getDate(),
                reservation.getTime(), reservation.getTheme(), reservation.getStore());
    }

    public int delete(Long id) {
        String sql = "DELETE FROM reservation WHERE id = ?;";
        return jdbcTemplate.update(sql, id);
    }

    public List<Reservation> findReservationsByThemeAndDate(Long themeId, LocalDate date) {
        String sql = "SELECT\n" +
                "    r.id as reservation_id,\n" +
                "    m.id as member_id,\n" +
                "    m.name as member_name,\n" +
                "    m.email,\n" +
                "    m.password,\n" +
                "    m.role as member_role,\n" +
                "    r.store_id,\n" +
                "    s.name as store_name,\n" +
                "    r.date,\n" +
                "    rt.id as time_id,\n" +
                "    rt.start_at as time_value,\n" +
                "    t.id as theme_id,\n" +
                "    t.name as theme_name,\n" +
                "    t.description,\n" +
                "    t.thumbnail\n" +
                "FROM reservation as r\n" +
                "INNER JOIN member as m\n" +
                "  ON r.member_id = m.id\n" +
                "INNER JOIN store as s\n" +
                "  ON r.store_id = s.id\n" +
                "INNER JOIN reservation_time as rt\n" +
                "  ON r.time_id = rt.id\n" +
                "INNER JOIN theme as t\n" +
                "  ON r.theme_id = t.id\n" +
                "WHERE t.id = ? "
                + "AND r.date = ?";

        return jdbcTemplate.query(sql, reservationRowMapper, themeId, date);
    }

    public boolean existsByStoreIdAndDateAndTimeAndTheme(Long storeId, LocalDate date, Long timeId, Long themeId) {
        String sql = "SELECT count(*) FROM reservation WHERE store_id = ? AND date = ? AND time_id = ? AND theme_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, storeId, date, timeId, themeId);
        return count != null && count > 0;
    }

    public Optional<Reservation> findById(Long id) {
        String sql = "SELECT\n" +
                "    r.id as reservation_id,\n" +
                "    m.id as member_id,\n" +
                "    m.name as member_name,\n" +
                "    m.email,\n" +
                "    m.password,\n" +
                "    m.role as member_role,\n" +
                "    r.store_id,\n" +
                "    s.name as store_name,\n" +
                "    r.date,\n" +
                "    rt.id as time_id,\n" +
                "    rt.start_at as time_value,\n" +
                "    t.id as theme_id,\n" +
                "    t.name as theme_name,\n" +
                "    t.description,\n" +
                "    t.thumbnail\n" +
                "FROM reservation as r\n" +
                "INNER JOIN member as m\n" +
                "  ON r.member_id = m.id\n" +
                "INNER JOIN store as s\n" +
                "  ON r.store_id = s.id\n" +
                "INNER JOIN reservation_time as rt\n" +
                "  ON r.time_id = rt.id\n" +
                "INNER JOIN theme as t\n" +
                "  ON r.theme_id = t.id\n" +
                "WHERE r.id = ?";
        List<Reservation> result = jdbcTemplate.query(sql, reservationRowMapper, id);
        return result.stream().findAny();
    }

    public boolean existsById(Long id) {
        String sql = "SELECT count(*) FROM reservation WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    public boolean existsByTimeId(Long timeId) {
        String sql = "SELECT count(*) FROM reservation WHERE time_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, timeId);
        return count != null && count > 0;
    }

    public boolean existsByThemeId(Long themeId) {
        String sql = "SELECT count(*) FROM reservation WHERE theme_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, themeId);
        return count != null && count > 0;
    }

    public void updateByDateAndTime(Long id, LocalDate date, Long timeId) {
        String sql = "UPDATE reservation SET date = ?, time_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, date, timeId, id);
    }

    private final RowMapper<Reservation> reservationRowMapper = (resultSet, rowNum) -> {
        ReservationTime time = new ReservationTime(
                resultSet.getLong("time_id"),
                resultSet.getObject("time_value", LocalTime.class));
        Theme theme = new Theme(
                resultSet.getLong("theme_id"),
                resultSet.getString("theme_name"),
                resultSet.getString("description"),
                resultSet.getString("thumbnail"));
        Member member = new Member(
                resultSet.getLong("member_id"),
                resultSet.getString("member_name"),
                resultSet.getString("email"),
                resultSet.getString("password"),
                Role.valueOf(resultSet.getString("member_role")));
        Store store = new Store(
                resultSet.getLong("store_id"),
                resultSet.getString("store_name"));

        return new Reservation(
                resultSet.getLong("reservation_id"),
                member,
                resultSet.getObject("date", LocalDate.class),
                time,
                theme,
                store);
    };
}
