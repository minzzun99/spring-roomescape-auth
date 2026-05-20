package roomescape.domain;

public class Member {
    private static final int MAX_LENGTH = 50;

    private final Long id;
    private final String name;
    private final String email;
    private final String password;

    public Member(Long id, String name, String email, String password) {
        validateName(name);
        validateEmail(email);
        validatePassword(password);

        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public Member(String name, String email, String password) {
        this(null, name, email, password);
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 비어 있을 수 없습니다. 이름을 입력해주세요.");
        }
        if (name.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("이름은 %d자를 넘을 수 없습니다. %d자 이내로 입력해주세요.", MAX_LENGTH, MAX_LENGTH));
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일은 비어 있을 수 없습니다. 이메일을 입력해주세요.");
        }
        if (email.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("이메일은 %d자를 넘을 수 없습니다. %d자 이내로 입력해주세요.", MAX_LENGTH, MAX_LENGTH));
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 비어 있을 수 없습니다. 비밀번호를 입력해주세요.");
        }
        if (password.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("비밀번호는 %d자를 넘을 수 없습니다. %d자 이내로 입력해주세요.", MAX_LENGTH, MAX_LENGTH));
        }
    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isSameId(Member member) {
        return this.id != null && this.id.equals(member.id);
    }
}
