package roomescape.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.domain.Member;
import roomescape.exception.AuthenticationException;
import roomescape.repository.MemberRepository;

public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";
    private static final String REQUIRED_LOGIN = "로그인이 필요합니다. 로그인 후 다시 시도해주세요.";

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    public LoginMemberArgumentResolver(MemberRepository memberRepository, TokenProvider tokenProvider) {
        this.memberRepository = memberRepository;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class) && Member.class.isAssignableFrom(
                parameter.getParameterType());
    }

    @Nullable
    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String authorization = request.getHeader(AUTHORIZATION);

        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            throw new AuthenticationException("로그인이 필요합니다.");
        }

        String token = authorization.substring(BEARER_PREFIX.length());
        Long memberId;
        try {
            memberId = tokenProvider.extractMemberId(token);
        } catch (RuntimeException e) {
            throw new AuthenticationException(REQUIRED_LOGIN);
        }

        return memberRepository.findById(memberId)
                .orElseThrow(() -> new AuthenticationException(REQUIRED_LOGIN));
    }
}
