package cz.cvut.kbss.analysis.security;

import cz.cvut.kbss.analysis.exception.InvalidJwtAuthenticationException;
import cz.cvut.kbss.analysis.service.JwtTokenProvider;
import cz.cvut.kbss.analysis.service.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtTokenFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityUtils securityUtils;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider, SecurityUtils securityUtils) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.securityUtils = securityUtils;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {

        String REGISTER_PATH = "/auth/register";
        String LOGIN_PATH ="/auth/signin";

        String path = ((HttpServletRequest) req).getRequestURI();
        String authHeader = ((HttpServletRequest) req).getHeader("Authorization");

        if (path.endsWith(LOGIN_PATH) || (path.endsWith(REGISTER_PATH) && authHeader.startsWith("Bearer undefined"))) {
            filterChain.doFilter(req, res);
            return;
        }

        try {
            String token = jwtTokenProvider.resolveToken((HttpServletRequest) req);
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                securityUtils.setCurrentUser(auth);
            }
        } catch (InvalidJwtAuthenticationException e) {
            log.error("Unauthorized request", e);
            ((HttpServletResponse) res).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(req, res);
    }
}
