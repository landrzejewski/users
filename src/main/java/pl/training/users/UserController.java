package pl.training.users;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController {

    @GetMapping("users")
    public String users() {
        return "users";
    }

    @GetMapping("admins")
    public String admins() {
        return "admins";
    }

    @GetMapping("me")
    public Object me(Authentication authentication, Principal principal) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getPrincipal();
    }


}
