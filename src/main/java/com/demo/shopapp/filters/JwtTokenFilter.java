package com.demo.shopapp.filters;


import com.demo.shopapp.components.JwtTokenUtils;
import com.demo.shopapp.entities.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.*;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    @Value("${api.prefix}")
    private String apiPrefix;

    private final UserDetailsService userDetailsService;
    private final JwtTokenUtils jwtTokenUtils;


    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try{
            if (isByPassToken(request)) {
                // cho đi qua custom filter change
                filterChain.doFilter(request, response);
                return;
            }
            // lấy giá trị của header HTTP với tên "Authorization" từ request
            final String authHeader = request.getHeader("Authorization");
            final String token = authHeader.substring(7);
            final String phoneNumber = jwtTokenUtils.extractPhoneNumber(token);

            if(authHeader == null && !authHeader.startsWith("Bearer ")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }

            if (phoneNumber != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                User userDetails = (User) userDetailsService.loadUserByUsername(phoneNumber);
                if(jwtTokenUtils.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            filterChain.doFilter(request, response); //enable bypass


            // nếu chưa xác thực nhận request auth

//            if ((authHeader != null) && authHeader.startsWith("Bearer ") ) {
//                // Xử lý xác thực nếu chưa có thông tin Authentication trong Security Context
//                final String token = authHeader.substring(7);
//                // giải mã token -> phone number
//                final String phoneNumber = jwtTokenUtils.extractPhoneNumber(token);
//                if (phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                    UserDetails userDetails =  userDetailsService.loadUserByUsername(phoneNumber);
//                    if(jwtTokenUtils.validateToken(token, userDetails)) {
//                        UsernamePasswordAuthenticationToken authenticationToken =
//                                new UsernamePasswordAuthenticationToken(userDetails,
//                                        null,
//                                        userDetails.getAuthorities());
//                        authenticationToken.setDetails(new
//                                WebAuthenticationDetailsSource().buildDetails(request));
//                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//                    }
//                    filterChain.doFilter(request, response);
//                }
//            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Set the HTTP response status

        }
    }

    private boolean isByPassToken(@NonNull HttpServletRequest request) {
        final List<Pair<String, String>> byPassTokens = List.of(
                Pair.of(apiPrefix + "/products", "GET"),
                Pair.of(apiPrefix + "/categories", "GET"),
                Pair.of(apiPrefix + "/roles", "GET"),
                Pair.of(apiPrefix + "/users/login", "POST"),
                Pair.of(apiPrefix + "/users/register", "POST")
        );

        for (Pair<String, String> byPassToken : byPassTokens) {
            if(request.getServletPath().contains(byPassToken.getKey())  &&
                    request.getMethod().equals(byPassToken.getValue())  ){
               return true;
            }
        }
        return false;
    }

}
