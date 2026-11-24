package pl.training.users;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.function.Function;
import java.util.function.Supplier;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class GenericAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_SEPARATOR = " ";

    private final AuthenticationConfiguration authenticationConfiguration;
    private final String prefix;
    private final Function<String, Authentication> authenticationProvider;

    public GenericAuthenticationFilter(AuthenticationConfiguration authenticationConfiguration, String prefix, Function<String, Authentication> authenticationProvider) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.prefix = prefix;
        this.authenticationProvider = authenticationProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authHeader = request.getHeader(AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith(prefix)) {
            var credentials = authHeader.substring(prefix.length() + TOKEN_SEPARATOR.length());
            var requestAuthentication = authenticationProvider.apply(credentials);
            try {
                var authentication = authenticationConfiguration.getAuthenticationManager()
                        .authenticate(requestAuthentication);
                save(authentication);
            } catch (AuthenticationException authenticationException) {
                response.setStatus(SC_UNAUTHORIZED);
            }
        }
        filterChain.doFilter(request, response);
    }

    private void save(Authentication authentication) {
        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

}
