package jway.gmao.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jway.gmao.dao.RolesRepo;
import jway.gmao.dao.UserPrivilegeRepo;
import jway.gmao.model.Menu;
import jway.gmao.model.RoleUtilisateur;
import jway.gmao.model.UserPrivilege;
import jway.gmao.model.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {
    private static final long serialVersionUID = -2550185165626007488L;

    @Autowired
    private UserPrivilegeRepo userPrivilegeRepo;
@Autowired
    private RolesRepo rolesRepo;

    //retrieve username from jwt token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    //retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    //for retrieveing any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(SecurityConstants.SECRET).parseClaimsJws(token).getBody();
    }

    //check if the token has expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //generate token for user
    public String generateToken(Utilisateur user) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, user);
    }

    //while creating the token -
    //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    //2. Sign the JWT using the HS512 algorithm and secret key.
    //3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    //   compaction of the JWT to a URL-safe string
    private String doGenerateToken(Map<String, Object> claims, Utilisateur user) {

        boolean isAdmin=false;
        List<String> roles = new ArrayList<>();

        RoleUtilisateur admin = rolesRepo.finRoleAdmin();
        if(user.getRoles() != null && user.getRoles().contains(admin)) {
            isAdmin=true;
            roles.add("ADMIN");
        }
        List<String> codes = userPrivilegeRepo.findAssociatedCodes(user);

        List<Map<String,Long>> privileges = new ArrayList<>();
        List<Menu> menus = userPrivilegeRepo.findMenusAffectedToUtilisateur(user);
        for(Menu menu : menus){
            Map<String,Long> privilege = new HashMap<>();
            long priv =1 ; //NON AUTORISÉ
            UserPrivilege p = userPrivilegeRepo.findPrivilegeByUtilisateurAndMenuCode(user.getId(), menu.getCode());
            if(p != null) priv=p.getPrivilege().getId();
            privilege.put(menu.getCode(),priv);
            privileges.add(privilege);
        }
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME * 1000))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET)
                .claim("privileges", privileges)
                .claim("codes", codes)
                .claim("roles", roles)
                .claim("isAdmin", isAdmin)
                .claim("idUser", user.getId())
                .compact();

        return token;
    }

    //validate token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
