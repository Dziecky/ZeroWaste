package projekt.zespolowy.zero_waste.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Qualifier("filterChain")
    @Autowired 
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private MockMvc mockMvc;



    @Test
    public void testPasswordEncoderBean() {
        assertThat(passwordEncoder).isNotNull();
        assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    public void testAuthenticationManagerBean() {
        assertThat(authenticationManager).isNotNull();
    }

    @Test
    public void testSecurityFilterChainBean() {
        assertThat(securityFilterChain).isNotNull();
    }



    @Test
    public void whenAccessProtectedEndpoints_thenRedirectToLogin() throws Exception {
        String[] securedUrls = {
                "/products/showFormForAddProduct",
                "/products/save"
        };

        for (String url : securedUrls) {
            mockMvc.perform(get(url))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Test
    public void whenLoggingIn_thenAuthenticateUser() throws Exception {
        mockMvc.perform(formLogin("/login")
                        .user("username", "user")
                        .password("password", "password"))
                .andExpect(status().is3xxRedirection());
    }
}
