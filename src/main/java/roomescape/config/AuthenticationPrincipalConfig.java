package roomescape.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.auth.LoginCheckInterceptor;
import roomescape.auth.LoginMemberArgumentResolver;
import roomescape.auth.TokenExtractor;
import roomescape.auth.TokenProvider;
import roomescape.repository.MemberRepository;

@Configuration
public class AuthenticationPrincipalConfig implements WebMvcConfigurer {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final TokenExtractor tokenExtractor;

    public AuthenticationPrincipalConfig(MemberRepository memberRepository, TokenProvider tokenProvider,
                                         TokenExtractor tokenExtractor) {
        this.memberRepository = memberRepository;
        this.tokenProvider = tokenProvider;
        this.tokenExtractor = tokenExtractor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor(tokenProvider, tokenExtractor))
                .addPathPatterns("/reservations/**")
                .excludePathPatterns("/login",
                        "/logout",
                        "/themes/weekly-top");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver(memberRepository, tokenProvider, tokenExtractor));
    }
}
