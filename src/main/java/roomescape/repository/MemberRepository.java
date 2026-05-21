package roomescape.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import roomescape.domain.Member;
import roomescape.domain.Role;

@Repository
public class MemberRepository {

    private final JdbcTemplate jdbcTemplate;

    public MemberRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Member> findByEmail(String email) {
        String sql = "SELECT id, name, email, password, role FROM member WHERE email = ?;";
        List<Member> result = jdbcTemplate.query(sql, memberRowMapper, email);
        return result.stream().findAny();
    }

    public Optional<Member> findById(Long memberId) {
        String sql = "SELECT id, name, email, password, role FROM member WHERE id = ?;";
        List<Member> result = jdbcTemplate.query(sql, memberRowMapper, memberId);
        return result.stream().findAny();
    }

    private final RowMapper<Member> memberRowMapper = (resultSet, rowNum) -> {
        return new Member(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("email"),
                resultSet.getString("password"),
                Role.valueOf(resultSet.getString("role")));
    };
}
