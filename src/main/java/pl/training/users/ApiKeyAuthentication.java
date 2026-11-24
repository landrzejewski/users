package pl.training.users;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.List;

public class ApiKeyAuthentication extends AbstractAuthenticationToken {

    private final String apiKey;

    public ApiKeyAuthentication(String apiKey) {
        super(List.of());
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    @Override
    public @Nullable Object getCredentials() {
        return apiKey;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return null;
    }

}
