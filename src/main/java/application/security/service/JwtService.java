package application.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    private final String SECRETE_KEY;
    private final long ACCESS_TOKEN_EXPIRE_MINUTES;

    public JwtService(
            @Value("${jwt.secret}") String secreteKey,
            @Value("${jwt.access_token_minutes}") long accessTokenExpireMinutes) {
        SECRETE_KEY = secreteKey;
        ACCESS_TOKEN_EXPIRE_MINUTES = accessTokenExpireMinutes;
    }

    public String generateToken(Map<String, Object> claims, String username, String user_id){

        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(ACCESS_TOKEN_EXPIRE_MINUTES);
        String jti = UUID.randomUUID().toString();

        claims.put(
                "user", username
        );
        claims.put("user_id", user_id);

        return Jwts.builder()
                .claims(claims)
                .id(jti)
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(Instant.ofEpochSecond(ACCESS_TOKEN_EXPIRE_MINUTES)))
                .signWith(getSignKey())
                .compact();

    }

    private SecretKey getSignKey(){
        byte[] keyByte = Decoders.BASE64.decode(SECRETE_KEY);
        return Keys.hmacShaKeyFor(keyByte);
    }

    public boolean validateToken(String token, UserDetails userDetails)throws JwtException {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));

    }
    public String extractUsername(String token){
        return getClaims(token, Claims::getSubject);
    }

    private <T> T getClaims(String token, Function<Claims, T> claimResolver){
        final Claims claims = extratAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extratAllClaims (String token){

        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token){
        return getExpirationDate(token).before(new java.util.Date());
    }

    private Date getExpirationDate(String token){
        return (Date) getClaims(token, Claims::getExpiration);
    }
}
