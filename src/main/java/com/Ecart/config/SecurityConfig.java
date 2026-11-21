// package com.Ecart.config;

// import com.Ecart.security.JwtAuthenticationFilter;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.AuthenticationProvider;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.CorsConfigurationSource;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// import java.util.Arrays;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {
    
   
// 	private final JwtAuthenticationFilter jwtAuthFilter;
//     private final AuthenticationProvider authenticationProvider;

//      public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, AuthenticationProvider authenticationProvider) {
// 	 	super();
// 	 	this.jwtAuthFilter = jwtAuthFilter;
// 	 	this.authenticationProvider = authenticationProvider;
// 	 }

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//             .cors().and()
            
//             .csrf().disable()
//             .authorizeHttpRequests()
//             .requestMatchers("/", "/error").permitAll()
//             .requestMatchers("/api/auth/customers/**","/delete/**","/cart/delete/**","/products/**","/cart/clearCart/**","/api/customers/profile/**","/products/**","/api/auth/customers/getallUser/**","/api/auth/customers/update/**","/api/auth/register","/api/products/search?/**","cart/delete/byProduct/**","/api/auth/login","/cart/**","/update/quantity/**","/api/feedback/**"," /api/feedback/add/**","/api/feedback/product/**","/api/products/**","/api/products/additem/**","/api/products/26/upload-image/**","/api/feedback/submit/**","/api/orders/all/**","/api/auth/logout/**","/api/auth/check-session/**").permitAll()
//             .requestMatchers("/api/products/**","/api/auth/check-customer/**","/api/products/*/upload-image/**","/api/auth/check-admin/**","/api/products/allitems","/admin/**","/api/orders/**", "/admin/admin-login", "/admin/admin-logout","/admin/check-admin/**","/admin/view-customers/**","/admin/add-products/**").permitAll()
//             .anyRequest().authenticated()
//             .and()
//             .sessionManagement()
//             .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//             .and()
//             .authenticationProvider(authenticationProvider)
//             .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
//         return http.build();
//     }

//     @Bean
//     public CorsConfigurationSource corsConfigurationSource() {
//         CorsConfiguration configuration = new CorsConfiguration();
//         configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
//         configuration.setAllowedOrigins(Arrays.asList( "https://ecartmanagement.netlify.app"));
//          // Angular default port
//         configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//         configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
//         configuration.setAllowCredentials(true);
        
//         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//         source.registerCorsConfiguration("/**", configuration);
//         return source;
//     }
    
    
// }



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
                    "/api/products/26/upload-image/**",
                    "/api/feedback/submit/**",
                    "/api/orders/all/**",
                    "/api/auth/logout/**",
                    "/api/auth/check-session/**"
                ).permitAll()

                .requestMatchers(
                    "/api/auth/check-customer/**",
                    "/api/products/*/upload-image/**",
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
