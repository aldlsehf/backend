package org.studymate.backend.security.jwt.provider;


import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.studymate.backend.security.jwt.token.JwtAuthenticationToken;
import org.studymate.backend.security.jwt.util.JwtTokenizer;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtTokenizer jwtTokenizer;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) authentication;
        // 토큰을 검증한다. 기간이 만료되었는지, 토큰 문자열이 문제가 있는지 등 Exception이 발생한다.
        Claims claims = jwtTokenizer.parseAccessToken(authenticationToken.getToken());
        // sub에 암호화된 데이터를 집어넣고, 복호화하는 코드를 넣어줄 수 있다.
        String email = claims.getSubject();
        List<GrantedAuthority> authorities = getGrantedAuthorities(claims);

        return new JwtAuthenticationToken(authorities, email, null);
    }

    private List<GrantedAuthority> getGrantedAuthorities(Claims claims) {
        List<String> roles = (List<String>) claims.get("roles");
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(() -> role);
        }
        return authorities;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}