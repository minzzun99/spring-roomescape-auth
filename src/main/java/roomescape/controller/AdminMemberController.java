package roomescape.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.dto.MemberResponse;
import roomescape.repository.MemberRepository;

@RestController
@RequestMapping("/admin/members")
public class AdminMemberController {

    private final MemberRepository memberRepository;

    public AdminMemberController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @GetMapping
    public List<MemberResponse> getMembersByName(@RequestParam(value = "name", required = false) String name) {
        return memberRepository.findMembersByName(name)
                .stream()
                .map(MemberResponse::from)
                .toList();
    }
}
