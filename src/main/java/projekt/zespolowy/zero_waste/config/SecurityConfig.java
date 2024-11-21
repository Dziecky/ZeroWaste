package projekt.zespolowy.zero_waste.config;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import projekt.zespolowy.zero_waste.services.CustomOAuth2UserService;
import projekt.zespolowy.zero_waste.services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;


@Configuration
public class SecurityConfig {
    // Bean kodera haseł
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Menedżer uwierzytelniania
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Łańcuch filtrów bezpieczeństwa
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UserService userService, CustomOAuth2UserService oAuth2UserService) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/login", "/oauth2/**","/submitLogin", "/submitRegister", "/css/**", "/js/**").anonymous()
                        .anyRequest().permitAll() // Zezwól na wszystkie żądania podczas produkcji
                )
                .userDetailsService(userService)
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService)
                        )
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .csrf(AbstractHttpConfigurer::disable); // Wyłącz CSRF dla uproszczenia podczas produkcji

        return http.build();
    }
}
