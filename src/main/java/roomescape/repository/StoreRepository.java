package roomescape.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import roomescape.domain.Store;

@Repository
public class StoreRepository {

    private final JdbcTemplate jdbcTemplate;

    public StoreRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Store> findById(Long id) {
        String sql = "SELECT id, name FROM store WHERE id = ?;";
        List<Store> result = jdbcTemplate.query(sql, storeRowMapper, id);
        return result.stream().findAny();
    }

    public List<Store> findAll() {
        String sql = "SELECT id, name FROM store";
        return jdbcTemplate.query(sql, storeRowMapper);
    }

    private final RowMapper<Store> storeRowMapper = (resultSet, rowNum) -> {
        return new Store(
                resultSet.getLong("id"),
                resultSet.getString("name"));
    };
}
