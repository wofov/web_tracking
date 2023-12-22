package com.web_tracking.config.security;

import com.bellelanco_api.constants.WebConstants;
import com.bellelanco_api.entity.Admin;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtTokenProvider {



    private final UserDetailsService userDetailsService;


    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }


    public String createToken(String adminId, Admin.Role role,long adminSeq) {
        Claims claims = Jwts.claims().setSubject(adminId);
        claims.put(WebConstants.ROLE, role);
        claims.put(WebConstants.ADMIN_SEQ, adminSeq);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }


    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPK(token));
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                WebConstants.GAP,
                userDetails.getAuthorities());
    }


    public String getUserPK(String token) {
        return Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody().getSubject();
    }


    public String resolveToken(HttpServletRequest request) {
        return request.getHeader(WebConstants.AUTHORIZATION);
    }


    public boolean validateToken(String jwtToken) {

        try {

            if(StringUtils.isEmpty(jwtToken)
                    || !jwtToken.contains(WebConstants.JWTDECODER)){

                return false;

            }else {

                Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(jwtToken);

                return !claims.getBody().getExpiration().before(new Date());

            }

        } catch (ExpiredJwtException e) {
            log.info(WebConstants.EXPIRE_TOKEN);
            return false;
        }
    }


}
