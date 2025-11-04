package com.uber.gateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Value("${jwt.enabled:false}")
    private boolean jwtEnabled;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        if (!jwtEnabled) {
            return http
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(reg -> reg.anyExchange().permitAll())
                    .build();
        }
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(reg -> reg
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/api/booking/**").hasAuthority("SCOPE_booking.read")
                        .pathMatchers("/api/orchestrator/**").hasAuthority("SCOPE_orchestrator.write")
                        .pathMatchers("/api/notification/**").hasAuthority("SCOPE_notification.send")
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }
}
