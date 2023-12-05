package org.cynic.spring_stuff.framework.converter;

import java.util.Set;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;

public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        try {
            Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("USER"));

            return UsernamePasswordAuthenticationToken.authenticated(
                new DefaultOidcUser(
                    authorities,
                    new OidcIdToken(
                        source.getTokenValue(),
                        source.getIssuedAt(),
                        source.getExpiresAt(),
                        source.getClaims()
                    ),
                    new OidcUserInfo(source.getClaims())
                ),
                source.getTokenValue(),
                authorities
            );
        } catch (Exception e) {
            throw new InvalidBearerTokenException(e.getMessage(), e);
        }
    }
}
