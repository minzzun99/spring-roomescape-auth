package roomescape.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.auth.LoginCheckInterceptor;
import roomescape.auth.LoginMemberArgumentResolver;
import roomescape.repository.MemberRepository;

@Configuration
public class AuthenticationPrincipalConfig implements WebMvcConfigurer {

    private final MemberRepository memberRepository;

    public AuthenticationPrincipalConfig(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .addPathPatterns("/reservations/**",
                        "/admin/**")
                .excludePathPatterns("/login",
                        "/logout",
                        "/themes/weekly-top");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver(memberRepository));
    }
}
