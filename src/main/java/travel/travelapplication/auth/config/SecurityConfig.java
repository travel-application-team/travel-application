package travel.travelapplication.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import travel.travelapplication.auth.service.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { // 예외 처리 핸들러 추가해야 함
        http
            .csrf(CsrfConfigurer::disable)
            .authorizeHttpRequests((authorizeRequests) ->
                authorizeRequests
                    .requestMatchers("/user/**", "/tag", "/profile/**", "/user-plan/**",
                        "/myroom/**", "/places/**", "/plans/**").authenticated()
                    .requestMatchers("/manager/**").hasAnyRole("ADMIN")
                    .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                    .anyRequest().permitAll()
            )
            .oauth2Login((oauth2Login) ->
                oauth2Login
                    .loginPage("/loginForm")
                    .userInfoEndpoint((userInfo) ->
                        userInfo
                            .userService(customOAuth2UserService))
                    .defaultSuccessUrl("/home")
            )
            .logout((logout) ->
                logout
                    .logoutUrl("/logout") // logout url 다시 확인
                    .clearAuthentication(true)
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .logoutSuccessUrl("/home")
            );

        return http.build();
    }

}

