package seoul.AutoEveryDay.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import seoul.AutoEveryDay.dto.RegisterReq;
import seoul.AutoEveryDay.service.LoginService;

import static seoul.AutoEveryDay.service.LoginService.isAuthenticated;

@Controller
@RequiredArgsConstructor
@RequestMapping("/register")
@Slf4j(topic = "registerController")
public class RegisterController {
    private final LoginService loginService;

    @GetMapping("")
    public String register() {
        return "register";
    }
    @PostMapping("")
    public String register(@Validated RegisterReq registerReq) {
        if (isAuthenticated()) {
            return "redirect:/home";
        }
        try {
            loginService.register(registerReq);
        } catch (ResponseStatusException e) {
            log.error("회원가입 실패", e);
            return "redirect:/register?error";
        }
        return "login";
    }
}
