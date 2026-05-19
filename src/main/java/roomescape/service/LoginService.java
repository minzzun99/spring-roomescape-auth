package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.Member;
import roomescape.exception.AuthenticationException;
import roomescape.repository.MemberRepository;

@Service
public class LoginService {

    private final MemberRepository memberRepository;

    public LoginService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


    public Member login(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("이메일 또는 비밀번호가 일치하지 않습니다."));

        if (!member.getPassword().equals(password)) {
            throw new AuthenticationException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        return member;
    }
}
