package cz.cvut.kbss.analysis.security;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CsrfHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) httpServletRequest.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            Cookie cookie = WebUtils.getCookie(httpServletRequest, SecurityConstants.CSRF_COOKIE_NAME);
            String token = csrfToken.getToken();
            if (cookie == null || token != null && !token.equals(cookie.getValue())) {
                cookie = new Cookie(SecurityConstants.CSRF_COOKIE_NAME, token);
                cookie.setPath(SecurityConstants.COOKIE_URI);
                httpServletResponse.addCookie(cookie);
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}