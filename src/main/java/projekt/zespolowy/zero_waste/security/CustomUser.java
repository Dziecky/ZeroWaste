package projekt.zespolowy.zero_waste.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import projekt.zespolowy.zero_waste.entity.User;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;

import java.util.Collection;
import java.util.Map;

public class CustomUser implements UserDetails, OidcUser {

    // Dodatkowa metoda do pobrania obiektu User
    @Getter
    private final User user;
    private Map<String, Object> attributes;
    private OidcIdToken idToken;
    private OidcUserInfo userInfo;

    // Konstruktor dla użytkowników tradycyjnych
    public CustomUser(User user) {
        this.user = user;
    }

    // Konstruktor dla użytkowników OIDC
    public CustomUser(User user, Map<String, Object> attributes, OidcIdToken idToken, OidcUserInfo userInfo) {
        this.user = user;
        this.attributes = attributes;
        this.idToken = idToken;
        this.userInfo = userInfo;
    }

    // Metody z interfejsu UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities(); // Upewnij się, że metoda getAuthorities() jest poprawnie zaimplementowana w klasie User
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // Może być null dla użytkowników zalogowanych przez Google
    }

    @Override
    public String getUsername() {
        return user.getUsername(); // Nazwa użytkownika z bazy danych
    }

//    @Override
//    public boolean isAccountNonExpired() {
//        return user.isAccountNonExpired();
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return user.isAccountNonLocked();
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return user.isCredentialsNonExpired();
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return user.isEnabled();
//    }

    // Metody z interfejsu OidcUser
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return user.getUsername(); // Użyj nazwy użytkownika jako nazwy
    }

    @Override
    public Map<String, Object> getClaims() {
        return attributes;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    public OidcIdToken getIdToken() {
        return idToken;
    }

}
