package org.cynic.spring_stuff.framework.decoder;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import java.text.ParseException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;

public class CustomJwtDecoder implements JwtDecoder {

    private static final Converter<Map<String, Object>, Map<String, Object>> CLAIMS_CONVERTER =
        MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            JWT jwt = JWTParser.parse(token);
            Map<String, Object> headers = jwt.getHeader().toJSONObject();
            Map<String, Object> claims = Optional.of(jwt.getJWTClaimsSet())
                .map(JWTClaimsSet::toJSONObject)
                .map(CLAIMS_CONVERTER::convert)
                .orElseGet(Map::of);

            return Jwt.withTokenValue(token)
                .headers(it -> it.putAll(headers))
                .claims(it -> it.putAll(claims))
                .build();
        } catch (ParseException e) {
            throw new InvalidBearerTokenException(e.getMessage(), e);
        }
    }
}
