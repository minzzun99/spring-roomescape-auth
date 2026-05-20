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

    private static final String REQUIRED_LOGIN = "로그인이 필요합니다. 로그인 후 다시 시도해주세요.";

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final TokenExtractor tokenExtractor;

    public LoginMemberArgumentResolver(MemberRepository memberRepository, TokenProvider tokenProvider,
                                       TokenExtractor tokenExtractor) {
        this.memberRepository = memberRepository;
        this.tokenProvider = tokenProvider;
        this.tokenExtractor = tokenExtractor;
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
        if (request == null) {
            throw new AuthenticationException(REQUIRED_LOGIN);
        }

        String token = tokenExtractor.extract(request);
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
