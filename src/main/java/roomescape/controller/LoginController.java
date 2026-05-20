package roomescape.controller;


import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.TokenProvider;
import roomescape.controller.dto.LoginRequest;
import roomescape.controller.dto.LoginResponse;
import roomescape.domain.Member;
import roomescape.service.LoginService;

@RestController
public class LoginController {

    private final TokenProvider tokenProvider;
    private final LoginService loginService;

    public LoginController(TokenProvider tokenProvider, LoginService loginService) {
        this.tokenProvider = tokenProvider;
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Member member = loginService.login(request.email(), request.password());
        String accessToken = tokenProvider.createToken(member.getId());
        return ResponseEntity.ok(new LoginResponse(accessToken));
    }
}
