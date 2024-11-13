package com.bookmytour.security;
import org.springframework.web.filter.CorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/api/auth/**").permitAll()  // Permitir acceso sin autenticación a /auth
                        .requestMatchers("/api/categories/**").permitAll()  // Permitir acceso sin autenticación a /categories
                        .requestMatchers("/api/cities/**").permitAll()  // Permitir acceso sin autenticación a /cities
                        .requestMatchers("/api/tours/**").permitAll()           // Permitir acceso sin autenticación a /tours
                        .requestMatchers("/api/tour-features/**").permitAll()      // Permitir acceso sin autenticación a /tour-features
                        .requestMatchers("/api/tour-images/**").permitAll()    // Permitir acceso sin autenticación a /tour-images
                        .requestMatchers("/api/tour-features/{id}").permitAll()  // Acceso específico al endpoint por ID //Comienza en /11 (el id)
                        .requestMatchers("/api/tour-cities/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")  // Rutas de administración solo para ADMIN
                        .requestMatchers("/api/roles/**").hasRole("ADMIN")
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN") // Requiere autenticación para
                        .requestMatchers("/api/bookings/**").authenticated()  // Requiere autenticación para /bookings
                        .requestMatchers("/error", "/favicon.ico", "/robots.txt").permitAll()  // Ignorar rutas adicionales
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Agregar el filtro JWT para autenticación en las rutas protegidas
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
