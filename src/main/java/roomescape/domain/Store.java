package roomescape.domain;

public class Store {

    private final Long id;
    private final String name;

    public Store(Long id, String name) {
        validateName(name);

        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("매장 이름은 비어 있을 수 없습니다.");
        }

        if (name.length() > 50) {
            throw new IllegalArgumentException("매장 이름은 50자를 넘을 수 없습니다. 50자 이내로 입력해주세요.");
        }
    }
}
