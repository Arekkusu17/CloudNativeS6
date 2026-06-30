package cl.duoc.cloudnative.guias.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    public static final String ROL_GESTION_GUIAS = "GESTION_GUIAS";
    public static final String ROL_DESCARGA_GUIAS = "DESCARGA_GUIAS";

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**").disable())
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info").hasAnyRole(ROL_DESCARGA_GUIAS, ROL_GESTION_GUIAS)
                        .requestMatchers(HttpMethod.GET, "/api/guias/*/descargar")
                        .hasAnyRole(ROL_DESCARGA_GUIAS, ROL_GESTION_GUIAS)
                        .requestMatchers("/api/guias/**").hasRole(ROL_GESTION_GUIAS)
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                        jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter defaultScopesConverter = new JwtGrantedAuthoritiesConverter();

        Converter<Jwt, Collection<GrantedAuthority>> authoritiesConverter = jwt -> {
            Set<GrantedAuthority> authorities = new HashSet<>(defaultScopesConverter.convert(jwt));
            addClaimAuthorities(jwt.getClaim("extension_role"), authorities);
            addClaimAuthorities(jwt.getClaim("extension_Role"), authorities);
            addClaimAuthorities(jwt.getClaim("role"), authorities);
            addClaimAuthorities(jwt.getClaim("roles"), authorities);
            addClaimAuthorities(jwt.getClaim("extension_Roles"), authorities);
            return authorities;
        };

        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return authenticationConverter;
    }

    private void addClaimAuthorities(Object claimValue, Set<GrantedAuthority> authorities) {
        if (claimValue instanceof String role) {
            addSingleRole(role, authorities);
            return;
        }

        if (claimValue instanceof Collection<?> roles) {
            for (Object role : roles) {
                if (role instanceof String roleValue) {
                    addSingleRole(roleValue, authorities);
                }
            }
        }
    }

    private void addSingleRole(String role, Set<GrantedAuthority> authorities) {
        String normalizedRole = role == null ? "" : role.trim();
        if (normalizedRole.isEmpty()) {
            return;
        }

        String authority = normalizedRole.startsWith("ROLE_")
                ? normalizedRole.toUpperCase()
                : "ROLE_" + normalizedRole.toUpperCase();
        authorities.add(new SimpleGrantedAuthority(authority));
    }
}
