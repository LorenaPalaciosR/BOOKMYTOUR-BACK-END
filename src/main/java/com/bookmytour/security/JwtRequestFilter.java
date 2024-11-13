package com.bookmytour.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Imprimir encabezado de autenticación para verificar en los logs
        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + authHeader);

        // Continuar con la cadena de filtros sin realizar autenticación
        chain.doFilter(request, response);
    }
}

