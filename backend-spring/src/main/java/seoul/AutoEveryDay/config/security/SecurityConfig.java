package seoul.AutoEveryDay.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

import static org.springframework.security.config.Customizer.withDefaults;
import static seoul.AutoEveryDay.enums.PrivilegeEnum.*;
import static seoul.AutoEveryDay.enums.RoleEnum.*;

// spring security 설정 파일 입니다.
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity() // prePostEnabled 어노테이션 활성화
public class SecurityConfig {
    private final String[] WHITE_LIST = {
            // swagger
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/swagger-resources/**",
            // index
            "/",
            // register
            "/register",
            // login
            "/login",
            "/login/process",
            // logout
            "/logout",
    };

    @Bean
    protected SecurityFilterChain myConfig(HttpSecurity http) throws Exception {
        /* 허용 페이지 등록 */
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(WHITE_LIST).permitAll()  // 모든 사용자 허용 경로
                        .anyRequest().authenticated()) // 인증된 사용자만 허용 경로
                // 로그인
                .formLogin(form -> form
                        .loginPage("/login") // 로그인 페이지
                        .loginProcessingUrl("/login/process") // 로그인 Form Action Url
                        .usernameParameter("username") // 아이디 파라미터명 설정, default: username
                        .passwordParameter("password") // 패스워드 파라미터명 설정, default: password
                        .defaultSuccessUrl("/home") // 로그인 성공 후 이동 페이지
                        .permitAll()
                )
                // 로그아웃
                .logout(withDefaults()) // '/logout' 으로 로그아웃
                .csrf(AbstractHttpConfigurer::disable); // csrf 비활성화

        return http.build();
    }
    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** 아래는 역할별로 계층 구조를 설정해주는 기능. 하지만 당장 Role 말고 Authority를 사용중 */
//    @Bean
//    public RoleHierarchy roleHierarchy() {
//        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
//        String hierarchy = ROLE_ADMIN.name() + " > " + ROLE_ADVANCED_USER.name() + "\n"
//                + ROLE_ADVANCED_USER.name() + " > " + ROLE_USER.name();
//        roleHierarchy.setHierarchy(hierarchy);
//        return roleHierarchy;
//    }
//    @Bean
//    public DefaultWebSecurityExpressionHandler customWebSecurityExpressionHandler() {
//        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
//        expressionHandler.setRoleHierarchy(roleHierarchy());
//        return expressionHandler;
//    }
}