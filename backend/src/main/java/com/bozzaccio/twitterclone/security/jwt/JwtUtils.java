package com.bozzaccio.twitterclone.security.jwt;

import com.bozzaccio.twitterclone.security.JWTAuthException;
import com.bozzaccio.twitterclone.security.User;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.bozzaccio.twitterclone.util.ErrorUtils.*;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${security.jwtSecret}")
    private String jwtSecret;

    @Value("${security.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {
        User userPrincipal = (User) authentication.getPrincipal();

        return Jwts.builder().setSubject((userPrincipal.getUsername())).setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)).signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {

        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            throw new JWTAuthException(buildErrorMessage(BASE_JWT_ERROR, JWT, JWT_SIGNATURE_INVALID));
        } catch (MalformedJwtException e) {
            throw new JWTAuthException(buildErrorMessage(BASE_JWT_ERROR, JWT, JWT_TOKEN_INVALID));
        } catch (ExpiredJwtException e) {
            throw new JWTAuthException(buildErrorMessage(BASE_JWT_ERROR, JWT, JWT_EXPIRED));
        } catch (UnsupportedJwtException e) {
            throw new JWTAuthException(buildErrorMessage(BASE_JWT_ERROR, JWT, JWT_TOKEN_UNSUPPORTED));
        } catch (IllegalArgumentException e) {
            throw new JWTAuthException(buildErrorMessage(BASE_JWT_ERROR, JWT, JWT_CLAIM_INVALID));
        }
    }
}
