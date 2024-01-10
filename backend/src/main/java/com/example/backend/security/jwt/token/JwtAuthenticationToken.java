package com.example.backend.security.jwt.token;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private String token;
    private Object principal;
    private Object credentials;

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */
    public JwtAuthenticationToken(Collection<? extends GrantedAuthority> authorities, Object principal, Object credentials) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.setAuthenticated(true); // jwt로 회원 인증이 완료됨
    }

    public JwtAuthenticationToken(String token){
        super(null);
        this.token = token;
        this.setAuthenticated(false); // 회원 인증이 안됨
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
