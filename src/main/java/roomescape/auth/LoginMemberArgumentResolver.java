package roomescape.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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

    public LoginMemberArgumentResolver(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
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
        HttpSession session = request.getSession(false);

        if (session == null) {
            throw new AuthenticationException(REQUIRED_LOGIN);
        }

        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            throw new AuthenticationException(REQUIRED_LOGIN);
        }

        return memberRepository.findById(memberId)
                .orElseThrow(() -> new AuthenticationException(REQUIRED_LOGIN));
    }
}
