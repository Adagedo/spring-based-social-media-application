package application.security.filter;

import application.security.service.CustomUserDetailsService;
import application.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtCookieAuthenticationFilter extends OncePerRequestFilter {
    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request
     * @param response
     * @param filterChain
     */
    private final  String TOKEN_NAME;
    private final JwtService jwtService;

    private final CustomUserDetailsService customUserDetailsService;
    public JwtCookieAuthenticationFilter(@Value("${jwt.token_name}")String tokenName, JwtService jwtService,
                                         CustomUserDetailsService customUserDetailsService) {
        TOKEN_NAME = tokenName;
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/api/v1/auth/");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        if(shouldNotFilter(request)){
            filterChain.doFilter(request, response);
            return;
        }

        String token = null;
        String user_data = null;

        if(request.getCookies() != null){
            for (Cookie cookie: request.getCookies()){
                if(TOKEN_NAME.equals(cookie.getName())){
                    token = cookie.getValue();
                    user_data = jwtService.extractUsername(token);
                    break;
                }
            }
        }

        if(user_data != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails1 = this.customUserDetailsService.loadUserByUsername(user_data);
            if(token != null && jwtService.validateToken(token, userDetails1)){
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails1, null, userDetails1.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
