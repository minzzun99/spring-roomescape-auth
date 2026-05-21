package roomescape.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.domain.Member;
import roomescape.exception.AuthenticationException;
import roomescape.exception.AuthorizationException;
import roomescape.repository.MemberRepository;

public class AdminCheckInterceptor implements HandlerInterceptor {

    private final TokenProvider tokenProvider;
    private final TokenExtractor tokenExtractor;
    private final MemberRepository memberRepository;

    public AdminCheckInterceptor(TokenProvider tokenProvider, TokenExtractor tokenExtractor,
                                 MemberRepository memberRepository) {
        this.tokenProvider = tokenProvider;
        this.tokenExtractor = tokenExtractor;
        this.memberRepository = memberRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = tokenExtractor.extract(request);
        Long memberId;
        try {
            memberId = tokenProvider.extractMemberId(token);
        } catch (RuntimeException e) {
            throw new AuthenticationException("로그인이 필요합니다. 로그인 후 다시 시도해주세요.");
        }
        Member manager = memberRepository.findById(memberId)
                .orElseThrow(() -> new AuthenticationException("로그인이 필요합니다. 로그인 후 다시 시도해주세요."));

        if (!manager.isAdmin()) {
            throw new AuthorizationException("관리자 권한이 필요합니다. 관리자로 로그인 후 다시 시도해주세요.");
        }

        return true;
    }
}
