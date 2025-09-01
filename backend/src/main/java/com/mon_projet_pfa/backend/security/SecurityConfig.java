package com.mon_projet_pfa.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // ✅ Configuration CORS avant tout
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // ✅ Désactiver CSRF pour API REST
                .csrf(csrf -> csrf.disable())

                // ✅ Session stateless pour JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ✅ CONFIGURATION TRÈS PERMISSIVE POUR DÉVELOPPEMENT
                .authorizeHttpRequests(authz -> authz
                        // Endpoints d'authentification publics
                        .requestMatchers("/auth/**").permitAll()

                        // ✅ PERMETTRE TOUS LES ENDPOINTS API
                        .requestMatchers("/api/**").permitAll()

                        // ✅ PERMETTRE L'ACCÈS AUX RESSOURCES STATIQUES
                        .requestMatchers("/", "/login", "/profile", "/products", "/suppliers", "/purchases").permitAll()
                        .requestMatchers("/static/**", "/public/**", "/assets/**").permitAll()

                        // Endpoints publics
                        .requestMatchers("/h2-console/**").permitAll()

                        // ✅ PERMETTRE TOUT LE RESTE (mode développement)
                        .anyRequest().permitAll())

                // ✅ Désactiver la protection des frames pour H2 Console
                .headers(headers -> headers.frameOptions().disable())

                // ✅ Désactiver la redirection vers login
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable())

                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ✅ Permettre les credentials (cookies, headers d'auth)
        configuration.setAllowCredentials(true);

        // ✅ Origines autorisées - Plus permissif
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));

        // ✅ Headers autorisés
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // ✅ Méthodes HTTP autorisées
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // ✅ Headers exposés au frontend
        configuration.setExposedHeaders(Arrays.asList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}