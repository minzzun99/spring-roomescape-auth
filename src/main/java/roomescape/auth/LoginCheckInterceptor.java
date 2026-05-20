package roomescape.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.exception.AuthenticationException;

public class LoginCheckInterceptor implements HandlerInterceptor {

    private final TokenProvider tokenProvider;
    private final TokenExtractor tokenExtractor;

    public LoginCheckInterceptor(TokenProvider tokenProvider, TokenExtractor tokenExtractor) {
        this.tokenProvider = tokenProvider;
        this.tokenExtractor = tokenExtractor;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = tokenExtractor.extract(request);

        try {
            tokenProvider.extractMemberId(token);
        } catch (RuntimeException e) {
            throw new AuthenticationException("로그인이 필요합니다. 로그인 후 다시 시도해주세요.");
        }

        return true;
    }
}
