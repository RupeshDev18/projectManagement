package com.example.projectManagement.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtFilter(JwtProvider jwtProvider,CustomUserDetailsService customUserDetailsService){
        this.jwtProvider=jwtProvider;
        this.customUserDetailsService=customUserDetailsService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            // ============================================
            // STEP 1: Extract token from header
            // ============================================
            String token=extractToken(request.getHeader("Authorization"));
            if(token==null){
                filterChain.doFilter(request,response);
                return;
            }


            // ============================================
            // STEP 2: Validate JWT token
            // ============================================
            if(!jwtProvider.validateJWT(token)){
                log.warn("token validation failed");
                filterChain.doFilter(request,response);
                return;
            }



            // ============================================
            // STEP 3: Extract email from token
            // ============================================
            String email= jwtProvider.extractEmail(token);


            // ============================================
            // STEP 4: Load UserDetails from database
            // ============================================
            UserDetails userDetails= customUserDetailsService.loadUserByUsername(email);


            // ============================================
            // STEP 5: Create Authentication object with UserDetails
            // ============================================
            UsernamePasswordAuthenticationToken authentication=new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());


            // ============================================
            // STEP 6: Set request details
            // ============================================
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));


            // ============================================
            // STEP 7: Store in SecurityContext
            // ============================================
            SecurityContextHolder.getContext().setAuthentication(authentication);




        } catch (Exception ex) {
            log.error("Error in JWT Filter: {}", ex.getMessage());
        }
        filterChain.doFilter(request,response);

    }

    private  String extractToken(String authHeader) {
        if(authHeader==null) return null;
        if(!authHeader.startsWith("Bearer ")){
            log.debug("token doesn't start with Bearer");
            return null;
        }
        return authHeader.substring(7);
    }
}
