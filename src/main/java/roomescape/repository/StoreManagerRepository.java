package roomescape.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StoreManagerRepository {

    private final JdbcTemplate jdbcTemplate;

    public StoreManagerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Long> findStoreIdByMemberId(Long memberId) {
        String sql = "SELECT store_id FROM store_manager WHERE member_id = ?;";
        return jdbcTemplate.queryForList(sql, Long.class, memberId).stream().findAny();
    }

    public boolean existsByMemberIdAndStoreId(Long memberId, Long storeId) {
        String sql = "SELECT count(*) FROM store_manager WHERE member_id = ? AND store_id = ?;";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, memberId, storeId);
        return count != null && count > 0;
    }
}
