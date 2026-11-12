package com.example.CinemaApplication.Security;

import com.example.CinemaApplication.model.Account;
import com.example.CinemaApplication.repository.AccountRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.security.Security;

@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final AccountRepository accountRepository;
    private final CustomAuthenticationEntryPoint entryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header= request.getHeader("Authorization");
        if(header==null){
            filterChain.doFilter(request,response);
            return;
        }
        try {
            if(!header.startsWith("Bearer ") || header.length()==7){
                filterChain.doFilter(request,response);
                return;
            }
            if(SecurityContextHolder.getContext().getAuthentication() == null){

            String token = header.substring(7);
            String username= jwtService.extractSubject(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            Account account=  accountRepository.findByUsernameIgnoreCase(username).orElseThrow(()-> new BadCredentialsException("Bad Credentials!"));
            if(!jwtService.isValidToken(token,userDetails,account)){
                    throw new AuthenticationException(("INVALID_TOKEN"));
                }
            var authentication= UsernamePasswordAuthenticationToken.authenticated(userDetails,null,userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            var context=SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            }
        }catch (Exception e){
              SecurityContextHolder.clearContext();
              entryPoint.commence(request, response, new BadCredentialsException("INVALID_ACCESS_TOKEN"));
return;
        }
        filterChain.doFilter(request,response);
        return;
    }
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//          String header=request.getHeader("Authorization");
//          if(header==null){
//              filterChain.doFilter(request,response);
//              return;
//          }
//          try {
//              if (!header.startsWith("Bearer ") || header.length() == 7) {
//                  throw new BadCredentialsException("Invalid access Token!");
//              }
//              if (SecurityContextHolder.getContext().getAuthentication() == null) {
//
//                  String token = header.substring(7);
//                  String username = jwtService.extractUsername(token);
//                  UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//                  Account account = accountRepository.findByUsernameIgnoreCase(username).
//                          orElseThrow(() -> new UsernameNotFoundException("Username not found: " + username));
//                  if (jwtService.isTokenValid(token, userDetails, account)) {
//                      var authentication = UsernamePasswordAuthenticationToken.authenticated(userDetails, null, userDetails.getAuthorities());
//                      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                      var currentAuth = SecurityContextHolder.getContext().getAuthentication();
//
//                      var context = SecurityContextHolder.createEmptyContext();
//                      context.setAuthentication(authentication);
//                      SecurityContextHolder.setContext(context);
//                  }
//              }
//              }
//          catch(JwtException | IllegalArgumentException e){
//                  SecurityContextHolder.clearContext();
//                  entryPoint.commence(request, response, new BadCredentialsException("INVALID_ACCESS_TOKEN"));
//              }
//          filterChain.doFilter(request,response);
//          return;
//    }

}
