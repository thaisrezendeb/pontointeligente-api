package br.com.thaisrezendeb.pontointeligente.api.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil {

    static final String CLAIM_KEY_USERNAME = "sub";
    static final String CLAIM_KEY_ROLE = "role";
    static final String CLAIM_KEY_CREATED = "created";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Obtem username (email) contido no token JWT
     *
     * @param token
     * @return String
     */
    public String getUsernameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        }
        catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * Retorna a data de expiracao de um token JWT
     *
     * @param token
     * @return Date
     */
    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        }
        catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    /**
     * Gera um novo token (refresh)
     *
     * @param token
     * @return String
     */
    public String refreshToken(String token) {
        String refreshedToken;
        try {
            Claims claims = getClaimsFromToken(token);
            claims.put(CLAIM_KEY_CREATED, new Date());
            refreshedToken = generateToken(claims);
        }
        catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    /**
     * Verifica e retorna se um token JWT e' valido
     *
     * @param token
     * @return boolean
     */
    public boolean isValidToken(String token) {
        return !expiredToken(token);
    }

    private boolean expiredToken(String token) {
        Date dataExpiracao = this.getExpirationDateFromToken(token);
        if(dataExpiracao == null) {
            return false;
        }
        return dataExpiracao.before(new Date());
    }

    /**
     * Retorna um novo JWT Token baseado nos dados do usuario
     *
     * @param userDetails
     * @return String
     */
    public String getToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        userDetails.getAuthorities().forEach(authority ->
                claims.put(CLAIM_KEY_ROLE, authority.getAuthority()));
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    /**
     * Realiza o parse do token JWT para extrair as informacoes contidas no corpo
     *
     * @param token
     * @return Claims
     */
    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        }
        catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    /**
     * Retorna a data de expiracao com base na data atual
     *
     * @return Date
     */
    private Date generateDateExpiration() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    /**
     * Verifica se um token JWT esta expirado
     * @param token
     * @return boolean
     */
    private boolean expiratedToken(String token) {
        Date dataExpiracao = this.getExpirationDateFromToken(token);
        if(dataExpiracao == null) {
            return false;
        }
        return dataExpiracao.before(new Date());
    }

    /**
     * Gera um novo token JWT contendo os dados (claims) fornecidos
     *
     * @param claims
     * @return String
     */
    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder().setClaims(claims)
                .setExpiration(generateDateExpiration())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

}
