package com.box.common.security.filter;

import com.box.common.core.enums.ErrorCode;
import com.box.common.core.response.Result;
import com.box.common.core.util.JsonUtils;
import com.box.common.security.context.SecurityContextHolder;
import com.box.common.security.context.SecurityUser;
import com.box.common.security.properties.SecurityProperties;
import com.box.common.security.token.JwtTokenClaims;
import com.box.common.security.token.JwtTokenParser;
import com.box.common.security.token.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器。
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final SecurityProperties securityProperties;
    private final JwtTokenParser jwtTokenParser;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public JwtAuthenticationFilter(SecurityProperties securityProperties, JwtTokenParser jwtTokenParser) {
        this.securityProperties = securityProperties;
        this.jwtTokenParser = jwtTokenParser;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!securityProperties.isEnabled()) {
            return true;
        }
        String requestUri = request.getRequestURI();
        return securityProperties.getPermitPaths().stream().anyMatch(pattern -> antPathMatcher.match(pattern, requestUri));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = JwtTokenProvider.resolveToken(request.getHeader(securityProperties.getTokenHeader()), securityProperties.getTokenPrefix());
        if (token == null || token.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            JwtTokenClaims claims = jwtTokenParser.parse(token);
            SecurityUser securityUser = claims.toSecurityUser();
            SecurityContextHolder.set(securityUser);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    securityUser,
                    token,
                    claims.getPermissions().stream().map(SimpleGrantedAuthority::new).toList()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            writeUnauthorized(response, exception.getMessage());
        } finally {
            SecurityContextHolder.clear();
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
        }
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        if (response.isCommitted()) {
            return;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String actualMessage = (message == null || message.isBlank()) ? ErrorCode.UNAUTHORIZED.message() : message;
        response.getWriter().write(JsonUtils.toJson(Result.fail(ErrorCode.UNAUTHORIZED.code(), actualMessage)));
    }
}
