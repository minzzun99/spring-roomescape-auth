package roomescape.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.exception.AuthenticationException;

public class LoginCheckInterceptor implements HandlerInterceptor {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";
    private static final String REQUIRED_LOGIN = "로그인이 필요합니다. 로그인 후 다시 시도해주세요.";

    private final TokenProvider tokenProvider;

    public LoginCheckInterceptor(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authorization = request.getHeader(AUTHORIZATION);

        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            throw new AuthenticationException(REQUIRED_LOGIN);
        }

        String token = authorization.substring(BEARER_PREFIX.length());
        try {
            tokenProvider.extractMemberId(token);
        } catch (RuntimeException e) {
            throw new AuthenticationException(REQUIRED_LOGIN);
        }

        return true;
    }
}
