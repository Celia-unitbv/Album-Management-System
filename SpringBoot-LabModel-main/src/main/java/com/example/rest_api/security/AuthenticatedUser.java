package com.example.rest_api.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

@Getter @Setter
public class AuthenticatedUser extends User implements OidcUser, Serializable {
    private String email;
    private String name;
    private String password;
    private boolean isOAuth2;
    private Map<String, Object> claims;
    private OidcUserInfo userInfo;
    private OidcIdToken idToken;
    private Map<String, Object> attributes;

    /* User Details constructors */
    public AuthenticatedUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.email = username;
        this.password = password;
    }

    public AuthenticatedUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.email = username;
        this.password = password;
    }

    //public AuthenticatedUser(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, String email) {
    //    super(email, "", authorities); // Use email as the username here
    //    this.email = email;
    //    this.attributes = attributes;
    //    this.isOAuth2 = true; // Distinguish OAuth2 users
    //}
//
    //public AuthenticatedUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
    //    super(username, password, true, true, true, true, authorities);
    //    this.password = password;
    //    this.name = username;
    //}

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public <A> A getAttribute(String name) {
        return (A) attributes.get(name);
    }
}
