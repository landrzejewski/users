package pl.training.users;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.time.LocalDateTime;
import java.util.function.Supplier;

public class CustomAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    @Override
    public @Nullable AuthorizationResult authorize(Supplier<? extends @Nullable Authentication> authenticationSupplier, RequestAuthorizationContext context) {
        // var request = context.getRequest();
        var authentication = authenticationSupplier.get();
        var currentHour = LocalDateTime.now().getHour();
        var inWorkHours = currentHour >= 9 && currentHour <= 11;
        return () ->
                authentication != null
                        && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                        && inWorkHours;
    }

}
