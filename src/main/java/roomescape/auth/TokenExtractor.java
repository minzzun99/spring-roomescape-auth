package roomescape.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import roomescape.exception.AuthenticationException;

@Component
public class TokenExtractor {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String REQUIRED_LOGIN = "로그인이 필요합니다. 로그인 후 다시 시도해주세요.";

    public String extract(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION);

        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            throw new AuthenticationException(REQUIRED_LOGIN);
        }

        return authorization.substring(BEARER_PREFIX.length());
    }
}
