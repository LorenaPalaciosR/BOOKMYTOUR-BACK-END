package com.bookmytour.security;

import com.bookmytour.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter  {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth", "/api/public", "/api/check-database", "/api/tours"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        System.out.println("JwtRequestFilter está siendo aplicado para: " + requestPath);

        // Omitir el filtro en las rutas públicas
        if (isPublicPath(requestPath)) {
            chain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        // Obtener el token JWT del encabezado
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);  // Extrae el token después de "Bearer "
            System.out.println("Token JWT recibido: " + jwt);

            try {
                username = jwtUtil.extractUsername(jwt);  // Extrae el nombre de usuario del token
                System.out.println("Usuario extraído del token: " + username);
            } catch (Exception e) {
                System.out.println("Error al extraer el nombre de usuario del token JWT: " + e.getMessage());
            }
        }

        // Validación del token y autenticación
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtUtil.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                System.out.println("Usuario autenticado exitosamente: " + username);
            } else {
                System.out.println("Token JWT no válido para el usuario: " + username);
            }
        }

        chain.doFilter(request, response);
    }

    // Método para verificar si la ruta es pública
    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }
}
