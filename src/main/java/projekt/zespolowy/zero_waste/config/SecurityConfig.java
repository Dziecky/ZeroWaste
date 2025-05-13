package projekt.zespolowy.zero_waste.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import projekt.zespolowy.zero_waste.services.CustomOidcUserService;
import projekt.zespolowy.zero_waste.services.UserService;

@Configuration
@EnableMethodSecurity
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
    public SecurityFilterChain filterChain(HttpSecurity http, UserService userService, CustomOidcUserService customOidcUserService) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/products/showFormForAddProduct",
                                "/products/save")
                        .authenticated()
                        .requestMatchers(
                                "/",
                                "/static/**",
                                "/login",
                                "/oauth2/**",
                                "/submitRegister",
                                "/css/**",
                                "/styles/**",
                                "/js/**",
                                "/scripts/**",
                                "/images/**",
                                "/forgot-password",
                                "/reset-password",
                                "/products/list",
                                "/products/view/{id}",
                                "/food",
                                "/food/search",
                                "/faq",
                                "/about",
                                "/products/ended-auctions")
                        .permitAll()

                        .anyRequest().authenticated()
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
                                .oidcUserService(customOidcUserService)
                        )
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .csrf(AbstractHttpConfigurer::disable); // Wyłącz CSRF dla uproszczenia podczas produkcji

        return http.build();
    }
}
