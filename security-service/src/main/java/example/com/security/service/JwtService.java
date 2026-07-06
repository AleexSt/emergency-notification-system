package example.com.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${security.jwt.key}")
    private String key;

    @Value("${security.jwt.prefix}")
    private String prefix;


    public String generateJwt(UserDetails userDetails){
        return Jwts.builder()
                .claims()
                .add(new HashMap<String, Object>())
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .and()
                .signWith(getSignInKey())
                .compact();


    }

    public String extractJwt(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith(prefix)){
            return authHeader.substring(prefix.length());
        }
        return null;
    }

    public boolean isJwtValid(String jwt, UserDetails userDetails){
        String username = extractUsername(jwt);
        return username.equals(userDetails.getUsername()) && !isJwtExpired(jwt);
    }

    public String extractUsername(String jwt){
        return extractClaim(jwt, Claims::getSubject);
    }

    private boolean isJwtExpired(String jwt){
        return extractClaim(jwt, Claims::getExpiration).before(new Date());
    }

    private SecretKey getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver){
        Claims claims = extractAllClaims(jwt);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();

    }
}
