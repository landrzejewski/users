package pl.training.users;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {

    private static final GrantedAuthority DEFAULT_ROLE = new SimpleGrantedAuthority("ROLE_USER");

    private final Map<String, String> apiKeys = Map.of(
            "0001", "jan"
    );

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof ApiKeyAuthentication apiKeyAuthentication) {
            var apiKey = apiKeyAuthentication.getApiKey();
            var user = apiKeys.get(apiKey);
            if (user == null) {
                throw new BadCredentialsException("Invalid api key");
            }
            return new UsernamePasswordAuthenticationToken(user, apiKey, Set.of(DEFAULT_ROLE));
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthentication.class.isAssignableFrom(authentication);
    }

}
