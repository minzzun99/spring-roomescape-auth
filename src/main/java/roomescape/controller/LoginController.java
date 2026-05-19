package roomescape.controller;


import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.dto.LoginRequest;
import roomescape.domain.Member;
import roomescape.service.LoginService;

@RestController
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        Member member = loginService.login(request.email(), request.password());
        session.setAttribute("memberId", member.getId());

        return ResponseEntity.ok().build();
    }
}
