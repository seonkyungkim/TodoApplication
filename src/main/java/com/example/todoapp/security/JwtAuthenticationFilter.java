package com.example.todoapp.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            //요청에서 토큰 가져오기
            String token = parseBearerToken(request);
            log.info("Filter is running..");

            //토큰 검사하기. JWT이므로 인가서버에 요청하지 않고도 검증이 가능
            if(token!=null && !token.equalsIgnoreCase("null")){

                //userid가져오기. 위조된 경우에는 예외 처리
                String userId = tokenProvider.validateAndGetUserId(token);
                log.info("Authenticated user ID: " + userId);

                //인증완료. SecurityContextHolder에 등록해야 인증된 사용자라고 간주
                AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userId,          //<- AuthenticationPrincipal(또는 Principla)
                                        // 인증된 사용자의 정보. 문자열이 아니어도 된다. 보통 UserDetails라는 오브젝트를 넣기도 한다.
                        null,
                        AuthorityUtils.NO_AUTHORITIES
                );
                //오브젝트를 SecurityContext에 등록 -> 나중에 스프링이 컨트롤러 메서드에서 @AuthenticationPrincipal을 통해 오브젝트 가져올 수 있음
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(authentication);
                SecurityContextHolder.setContext(securityContext);
            }
        } catch(Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String parseBearerToken(HttpServletRequest request){
        //Http 요청의 헤더를 파싱해 Bearer토큰을 리턴
        String bearerToken = request.getHeader("Authentication");

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")){
            return bearerToken.substring(7);
        }
        return null;
    }

}
