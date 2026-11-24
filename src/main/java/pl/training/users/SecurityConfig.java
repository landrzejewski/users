package pl.training.users;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity(debug = true)
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public UserDetails user() {
        return User.builder()
                .username("user")
                .password(passwordEncoder().encode("123"))
                .roles("USER")
                .build();
    }

    public UserDetails admin() {
        return User.builder()
                .username("admin")
                .password(passwordEncoder().encode("123"))
                .roles("ADMIN")
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(user(), admin());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, GenericAuthenticationFilter  genericAuthenticationFilter) throws Exception {
        return http
                //.addFilterBefore(new WorkHoursFilter(), UsernamePasswordAuthenticationFilter.class)
                //.addFilterBefore(genericAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(config -> config.jwt(withDefaults()))
                .oauth2Login(config -> config.userInfoEndpoint(this::userInfoCustomizer))
                //.httpBasic(withDefaults())
                //.formLogin(withDefaults())
                .authorizeHttpRequests(config -> config
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/users").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/admins").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .build();
    }

    @Bean
    public GenericAuthenticationFilter genericAuthenticationFilter(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return new GenericAuthenticationFilter(authenticationConfiguration, "API_KEY", ApiKeyAuthentication::new);
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakAuthoritiesConverter());
        return jwtAuthenticationConverter;
    }


    // Client scopes -> Client scope details (roles) -> Mapper details -> Add to userinfo enabled (Keycloak Admin console)
    private void userInfoCustomizer(OAuth2LoginConfigurer<HttpSecurity>.UserInfoEndpointConfig userInfoEndpointConfig) {
        userInfoEndpointConfig.userAuthoritiesMapper(new KeycloakGrantedAuthoritiesMapper());
    }



}
