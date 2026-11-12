package com.example.CinemaApplication.Security;

import com.example.CinemaApplication.model.Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private Long jwtExpiration;
    @Value("${jwt.issuer}")
    private String jwtIssuer;
    @Value(("${jwt.audience}"))
    private String jwtAudience;
    public SecretKey getSigningKey(){
        byte[] bytes= Base64.getDecoder().decode(jwtSecret);
        return Keys.hmacShaKeyFor(bytes);
    }
    public String generateToken(UserDetails userDetails,Long accountId,int tokenVersion){
        return Jwts.builder().setClaims(Map.of("account_id",accountId,
                                         "token_version",tokenVersion,
                                          "roles",userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())).
                              setSubject(userDetails.getUsername()).
                              setAudience(jwtAudience).setIssuer(jwtIssuer).
                              setIssuedAt(
                                      new Date()).setExpiration(new Date(System.currentTimeMillis()+jwtExpiration)).
                signWith(getSigningKey(),SignatureAlgorithm.HS256).compact();
    }
    public Claims extractAllClaims(String token){
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).
                requireIssuer(jwtIssuer).requireAudience(jwtAudience).build().parseClaimsJws(token).getBody();
    }
    public <T> T extractClaim(String token,Function<Claims,T> resolverClaim){
        Claims claim= extractAllClaims(token);
        return resolverClaim.apply(claim);
    }
    public String extractSubject(String token){
        return extractClaim(token,Claims::getSubject);
    }
    public Long extractAccountId(String token){
        return Long.parseLong(extractAllClaims(token).get("account_id").toString());
    }
    public Long extractTokenVersion(String token){
        return Long.parseLong(extractAllClaims(token).get("token_version").toString());
    }
    public boolean isTokenExpired(String token){
        return extractClaim(token,Claims::getExpiration).before(new Date());
    }
    public boolean isValidToken(String token, UserDetails userDetails,Account account){
        return account.getUsername().equals(extractSubject(token)) &&
                account.getId().equals(extractAccountId(token)) &&
                account.getTokenVersion() ==extractTokenVersion(token) &&
                !isTokenExpired(token) && userDetails.isEnabled();
    }
//    public SecretKey getSigningKey(){
//        byte[] bytes= Base64.getDecoder().decode(jwtSecret);
//       return Keys.hmacShaKeyFor(bytes);
//    }
//  public String generateToken(UserDetails userDetails,Long accountId, int tokenVersion){
//        return Jwts.builder().
//                 setClaims(Map.of("accountId",accountId,
//                         "version",tokenVersion,
//                         "roles", userDetails.getAuthorities().stream().
//                                  map(GrantedAuthority::getAuthority).toList())).
//                setSubject(userDetails.getUsername()).
//                setIssuer(jwtIssuer).setAudience(jwtAudience).
//                setIssuedAt(new Date()).
//                setExpiration(new Date(System.currentTimeMillis()+jwtExpiration)).
//                setId(UUID.randomUUID().toString()).
//                signWith(getSigningKey(), SignatureAlgorithm.HS256).
//                compact();
//    }
//    public Claims extractAllClaims(String token){
//         return Jwts.parserBuilder().setSigningKey(getSigningKey()).
//                 requireIssuer(jwtIssuer).requireAudience(jwtAudience).build().parseClaimsJws(token).getBody();
//    }
//   public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
//        return claimsResolver.apply(extractAllClaims(token));
//    }
//   public boolean isExpiredToken(String token){
//        return extractClaim(token,Claims::getExpiration).before(new Date());
//    }
//    public String extractUsername(String token){
//        return extractClaim(token,Claims::getSubject);
//    }
//    public Long extractAccountId(String token){
//        return Long.parseLong(extractAllClaims(token).get("accountId").toString());
//    }
//    public Long extractVerionToken(String token){
//        return Long.parseLong(extractAllClaims(token).get("version").toString());
//    }
//    public boolean isTokenValid(String token, UserDetails userDetails, Account account){
//        return !isExpiredToken(token) &&
//                account.getId().equals(extractAccountId(token)) &&
//                account.getTokenVersion() == extractVerionToken(token) &&
//                account.getUsername().equals(extractUsername(token)) &&
//                userDetails.isEnabled();
//
//    }

}
