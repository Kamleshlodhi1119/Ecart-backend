package com.Ecart.config;

import com.Ecart.security.JwtAuthenticationFilter;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthFilter,
            AuthenticationProvider authenticationProvider) {

        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                .requestMatchers("/", "/error").permitAll()

                .requestMatchers("/api/products/*/upload-image").authenticated()

                .requestMatchers(
                    "/api/auth/customers/**",
                    "/delete/**",
                    "/cart/delete/**",
                    "/products/**",
                    "/cart/clearCart/**",
                    "/api/customers/profile/**",
                    "/api/auth/customers/getallUser/**",
                    "/api/auth/customers/update/**",
                    "/api/auth/register",
                    "/api/products/search",
                    "/cart/delete/byProduct/**",
                    "/api/auth/login",
                    "/cart/**",
                    "/update/quantity/**",
                    "/api/feedback/**",
                    "/api/feedback/add/**",
                    "/api/feedback/product/**",
                    "/api/products/**",
                    "/api/products/additem/**",
                    "/api/feedback/submit/**",
                    "/api/orders/all/**",
                    "/api/auth/logout/**",
                    "/api/auth/check-session/**"
                ).permitAll()

                .requestMatchers(
                    "/api/auth/check-customer/**",
                    "/api/auth/check-admin/**",
                    "/api/products/allitems",
                    "/admin/**",
                    "/api/orders/**",
                    "/admin/admin-login",
                    "/admin/admin-logout",
                    "/admin/check-admin/**",
                    "/admin/view-customers/**",
                    "/admin/add-products/**"
                ).permitAll()

                .anyRequest().authenticated()
            )

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authenticationProvider(authenticationProvider)

            .addFilterBefore(
                jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:4200",
            "https://ecartmanagement.netlify.app"
        ));

        configuration.setAllowedMethods(Arrays.asList(
            "GET",
            "POST",
            "PUT",
            "DELETE",
            "PATCH",
            "OPTIONS"
        ));

        configuration.setAllowedHeaders(Arrays.asList("*"));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
